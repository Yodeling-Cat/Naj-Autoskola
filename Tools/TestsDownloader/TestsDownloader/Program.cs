using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Net;
using System.Text;
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

        static string mUrl;
        static string sqlQuery = "";
        static int countOfDownloadedTests = 0;
        static int countOfDownloadedQuestions = 0;

        /* Settings */
        static string testsTable = "Tests";
        static string questionsTable = "Questions";
        // Prints the query into the command line
        static bool printSqlQuery = true;

        static void Main(string[] args)
        {
            Console.OutputEncoding = System.Text.Encoding.UTF8;
            try
            {
                // Format "Category=testIdxFrom-testIdxTo"
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
                " - tableName: " + testsTable + "\n" +
                " - printSqlQuery: " + printSqlQuery + "\n");

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
                foreach (String arg in args)
                {
                    // Get the range of tests to download
                    // Example "AB=1-35"
                    var argSplit = arg.Split('=');
                    var category = argSplit[0];

                    var idxRange = argSplit[1].Split('-');
                    var testIdxFrom = int.Parse(idxRange[0]);
                    var testIdxTo = int.Parse(idxRange[1]);

                    // For each test
                    for (int testIdx = testIdxFrom; testIdx < testIdxTo; testIdx++)
                    {
                        mUrl = "http://www.vodicak.sk/testy/test/" + testIdx + "/type/" + category + "/";

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
                        Console.WriteLine("URL: " + mUrl);
                        Console.WriteLine("TestIdx: " + testIdx);
                        Console.WriteLine("Category: " + category + "\n");

                        var container = wb.Document.GetElementById("zzztest");
                        var divs = container.GetElementsByTagName("div");

                        int questionId = 0;
                        var question = "";
                        int questionType = 0;
                        var imageName = "";
                        var correctAnswer = 0;
                        var points = 0;
                        var answer1 = "";
                        var answer2 = "";
                        var answer3 = "";


                        int questionIdx = 1;
                        foreach (HtmlElement div in divs)
                        {
                            // Question
                            if (div.GetAttribute("className") == " testy body question")
                            {
                                questionId = questionIdx - 1;
                                question = div.GetElementsByTagName("strong")[0].InnerText;
                                correctAnswer = int.Parse(wb.Document.GetElementById("correct_answer-" + questionIdx).GetAttribute("value"));
                                points = int.Parse(wb.Document.GetElementById("score-" + questionIdx).GetAttribute("value"));
                                answer1 = wb.Document.GetElementById("1-" + questionIdx).InnerText;
                                answer2 = wb.Document.GetElementById("2-" + questionIdx).InnerText;
                                answer3 = wb.Document.GetElementById("3-" + questionIdx).InnerText;

                                // Get the image name
                                if (div.GetElementsByTagName("img").Count != 0)
                                {
                                    var imageUrl = div.GetElementsByTagName("img")[0].GetAttribute("src");
                                    var withExtension = imageUrl.Split('/');
                                    imageName = withExtension[withExtension.Length - 1].Split('.')[0];

                                    // Check if the image is not an intersection
                                    var intersectionName = 0;
                                    int.TryParse(imageName, out intersectionName);

                                    if (intersectionName != questionIdx)
                                    {
                                        questionType = 1;
                                        imageName = "s:" + imageName;
                                    }
                                    else
                                    {
                                        questionType = 2;
                                        imageName = "i:" + imageName;
                                    }
                                }
                                else
                                {
                                    questionType = 0;
                                    imageName = "";
                                }

                                Console.WriteLine("questionIdx: " + questionIdx);
                                Console.WriteLine("question: " + question);
                                Console.WriteLine("image: " + imageName);
                                Console.WriteLine("correctAnswer: " + correctAnswer);
                                Console.WriteLine("points: " + points);
                                Console.WriteLine("answer1: " + answer1);
                                Console.WriteLine("answer2: " + answer2);
                                Console.WriteLine("answer3: " + answer3);

                                // Generate SQL query
                                /*sqlQuery += "INSERT INTO '" + testsTable + "' VALUES (" +
                                    i + ", '" + category + "', '" +
                                    identifier + "', '" +
                                    title.Replace("'", "''") + "', '" +
                                    imageName + "', '" +
                                    desc.Replace("'", "''") + "');\n";*/

                                /*sqlQuery += "INSERT INTO '" + questionsTable + "' VALUES (" +
                                    i + ", '" + category + "', '" +
                                    identifier + "', '" +
                                    title.Replace("'", "''") + "', '" +
                                    imageName + "', '" +
                                    desc.Replace("'", "''") + "');\n";*/

                                questionIdx++;
                                countOfDownloadedQuestions++;
                                Console.WriteLine();
                            }
                        }

                        if (printSqlQuery)
                        {
                            Console.WriteLine("\nsqlQuery for test #" + countOfDownloadedTests + ":\n" + sqlQuery);
                        }
                    }

                    // Save the SQL Query to a file
                    sqlQuery = "-- GENERATED WITH TestsDownloader v" + version + "\n" + sqlQuery;
                    System.IO.File.WriteAllText(category + ".sql", sqlQuery);

                    Console.WriteLine("\nJob well done.");

                    Console.WriteLine("Downloaded " + countOfDownloadedTests + " tests with " + countOfDownloadedQuestions + " questions total.");
                }

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