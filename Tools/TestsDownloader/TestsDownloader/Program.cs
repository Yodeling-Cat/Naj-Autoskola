using System;
using System.Text.RegularExpressions;
using System.Threading;
using System.Threading.Tasks;
using System.Windows.Forms;

/* Tool for downloading/updating the RoadSigns database table with content from http://www.vodicak.sk/testy/test/1/type/AB/ */
namespace TestsDownloader
{
	class Program
	{
		// TODO: Increment this if you make changes (especially breaking ones)
		// Incremental version code
		static int version = 1;

		/* Settings */
		static string testsTable = "Tests";
		static string questionsTable = "Questions";
		static string testsVersion = "1";
		static string questionsVersion = "1";
		static string testsVersionName = "2015";

		static string mUrl;
		static string TestsSQLQuery = "";
		static string QuestionsSQLQuery = "";
		static int countOfDownloadedTests = 0;
		static int countOfDownloadedQuestions = 0;

		static void Main(string[] args)
		{
			Console.OutputEncoding = System.Text.Encoding.UTF8;
			Console.WriteLine("TestsDownloader v" + version + "\n");
			try
			{
				// Format "Category=testsIdFrom-testsIdTo"
				// Note: Using the same category twice will probably overwrite the output file.
				var task = MessageLoopWorker.Run(DoWorkAsync,
					"AB=1-35",
					"CDT=36-60");
				task.Wait();
				//Console.WriteLine("DoWorkAsync completed.");
			}
			catch (Exception ex)
			{
				Console.WriteLine("DoWorkAsync failed: " + ex.Message);
				Console.WriteLine("Exception: " + ex.InnerException);
			}

			Console.WriteLine("\nPress Enter to exit.");
			Console.ReadLine();
		}

		// navigate WebBrowser to the list of urls in a loop
		static async Task<object> DoWorkAsync(object[] args)
		{
			Console.WriteLine("Using settings:\n" +
				" - testsTable: " + testsTable + "\n" +
				" - questionsTable: " + questionsTable + "\n" +
				" - testsVersion: " + testsVersion + "\n" +
				" - questionsVersion: " + questionsVersion + "\n" +
				" - testsVersionName: " + testsVersionName + "\n");

			using (var wb = new WebBrowser())
			{
				wb.ScriptErrorsSuppressed = true;

				TaskCompletionSource<bool> tcs = null;
				WebBrowserDocumentCompletedEventHandler documentCompletedHandler = (sender, e) =>
				{
					var targetPath = Regex.Split(mUrl, ".sk")[1];

					// Waits for the page to trully finish loading
					if ((sender as WebBrowser).Url.AbsolutePath != targetPath)
						return;

					tcs.TrySetResult(true);
				};

				// For each arg (which should be a category)
				foreach (string arg in args)
				{
					// Get the range of tests to download
					// Example "AB=1-35"
					var argSplit = arg.Split('=');
					var category = argSplit[0];

					var idRange = argSplit[1].Split('-');
					var testsIdFrom = int.Parse(idRange[0]);
					var testsIdTo = int.Parse(idRange[1]);

					// For each test
					for (int testId = testsIdFrom; testId <= testsIdTo; testId++)
					{
						mUrl = "http://www.vodicak.sk/testy/test/" + testId + "/type/" + category + "/";

						// Navigate to the URL
						tcs = new TaskCompletionSource<bool>();
						wb.DocumentCompleted += documentCompletedHandler;
						try
						{
							wb.Navigate(mUrl);
							// await for DocumentCompleted
							await tcs.Task;
						}
						finally
						{
							wb.DocumentCompleted -= documentCompletedHandler;
						}

						// the DOM is ready
						Console.WriteLine("===================================");
						Console.WriteLine("TestId: " + testId);
						Console.WriteLine("Category: " + category + "\n");
						Console.WriteLine("URL: " + mUrl);

						var container = wb.Document.GetElementById("zzztest");
						var divs = container.GetElementsByTagName("div");

						var questions = "";
						var question = "";
						int questionType = 0;
						var imageName = "";
						var correctAnswer = 0;
						var points = 0;
						var answer1 = "";
						var answer2 = "";
						var answer3 = "";

						// The index of this question between 1-27
						int questionIndex = 1;
						foreach (HtmlElement div in divs)
						{
							// For each question
							if (div.GetAttribute("className") == " testy body question")
							{
								var questionId = (27 * testId - 27 + questionIndex);
								question = div.GetElementsByTagName("strong")[0].InnerText;
								correctAnswer = int.Parse(wb.Document.GetElementById("correct_answer-" + questionId).GetAttribute("value"));
								points = int.Parse(wb.Document.GetElementById("score-" + questionId).GetAttribute("value"));
								answer1 = wb.Document.GetElementById("1-" + questionId).InnerText;
								answer2 = wb.Document.GetElementById("2-" + questionId).InnerText;
								answer3 = wb.Document.GetElementById("3-" + questionId).InnerText;

								// Get the image name
								if (div.GetElementsByTagName("img").Count != 0)
								{
									var imageUrl = div.GetElementsByTagName("img")[0].GetAttribute("src");
									var withExtension = imageUrl.Split('/');
									imageName = withExtension[withExtension.Length - 1].Split('.')[0];

									// Check if the image is not an intersection
									var temp = 0;
									var isNumber = int.TryParse(imageName, out temp);

									if (!isNumber)
									{
										// Type of Road Sign
										questionType = 1;
										imageName = "s:" + imageName;
									}
									else
									{
										// Type of Intersection
										questionType = 2;
										imageName = "i:" + imageName;
									}
								}
								else
								{
									// Type of Question
									questionType = 0;
									imageName = "";
								}

								Console.WriteLine("questionId: " + questionId);
								Console.WriteLine("questionIndex: " + questionIndex);
								Console.WriteLine("questionType: " + questionType);
								Console.WriteLine("question: " + question);
								Console.WriteLine("image: " + imageName);
								Console.WriteLine("correctAnswer: " + correctAnswer);
								Console.WriteLine("points: " + points);
								Console.WriteLine("answer1: " + answer1);
								Console.WriteLine("answer2: " + answer2);
								Console.WriteLine("answer3: " + answer3);

								// Escape the strings
								question = question.Replace("'", @"''");
								answer1 = answer1.Replace("'", @"''");
								answer2 = answer2.Replace("'", @"''");
								answer3 = answer3.Replace("'", @"''");

								// Construct the Questions SQL query
								QuestionsSQLQuery += "INSERT INTO \"" + questionsTable + "\" (question_id, type, version, question, image, points, correct_answer, answer1, answer2, answer3) VALUES ('" +
									questionId + "', '" +
									questionType + "', '" +
									questionsVersion + "', '" +
									question + "', '" +
									imageName + "', '" +
									points + "', '" +
									correctAnswer + "', '" +
									answer1 + "', '" +
									answer2 + "', '" +
									answer3 + "');\n";

								questions += questionId;
								if (questionIndex != 27) { questions += ","; }

                                questionIndex++;
								countOfDownloadedQuestions++;
								Console.WriteLine();
							}
						}

						// Construct the Tests SQL query
						TestsSQLQuery += "INSERT INTO '" + testsTable + "' (test_id, version_code, version_name, questions) VALUES (" +
							testId + ", " + testsVersion + ", '" +
							testsVersionName + "', '" +
							questions + "');\n";

						countOfDownloadedTests++;
                    }
				}

				// Write the SQL Queries to .sql files
				TestsSQLQuery = "-- GENERATED WITH TestsDownloader v" + version + "\n" + TestsSQLQuery;
				System.IO.File.WriteAllText("Tests.sql", TestsSQLQuery);

				QuestionsSQLQuery = "-- GENERATED WITH TestsDownloader v" + version + "\n" + QuestionsSQLQuery;
				System.IO.File.WriteAllText("Questions.sql", QuestionsSQLQuery);

				Console.WriteLine("\n**************");
				Console.WriteLine("Job well done.");
				Console.WriteLine("Downloaded " + countOfDownloadedTests + " tests with " + countOfDownloadedQuestions + " questions total.");

				return null;
			}

		}

		// a helper class to start the message loop and execute an asynchronous task
		public static class MessageLoopWorker
		{
			public static async Task<object> Run(Func<object[], Task<object>> worker, params object[] args)
			{
				var tcs = new TaskCompletionSource<object>();

				var thread = new Thread(() =>
				{
					EventHandler idleHandler = null;

					idleHandler = async (s, e) =>
					{
						// handle Application.Idle just once
						Application.Idle -= idleHandler;

						// return to the message loop
						await Task.Yield();

						// and continue asynchronously
						// propogate the result or exception
						try
						{
							var result = await worker(args);
							tcs.SetResult(result);
						}
						catch (Exception ex)
						{
							tcs.SetException(ex);
						}

						// signal to exit the message loop
						// Application.Run will exit at this point
						Application.ExitThread();
					};

					// handle Application.Idle just once
					// to make sure we're inside the message loop
					// and SynchronizationContext has been correctly installed
					Application.Idle += idleHandler;
					Application.Run();
				});

				// set STA model for the new thread
				thread.SetApartmentState(ApartmentState.STA);

				// start the thread and await for the task
				thread.Start();
				try
				{
					return await tcs.Task;
				}
				finally
				{
					thread.Join();
				}
			}
		}
	}
}