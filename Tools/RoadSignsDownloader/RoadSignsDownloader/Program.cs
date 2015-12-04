using nQuant;
using System;
using System.Collections.Generic;
using System.Drawing;
using System.Drawing.Imaging;
using System.IO;
using System.Linq;
using System.Net;
using System.Text;
using System.Text.RegularExpressions;
using System.Threading;
using System.Threading.Tasks;
using System.Windows.Forms;

/* Tool for downloading/updating the RoadSigns database table with content from http://www.vodicak.sk/znacky/ */
namespace RoadSignsDownloader
{
    class Program
    {
        // TODO: Increment this if you make changes (especially breaking ones)
        // Incremental version code
        static int version = 2;

        static string mUrl;
        static string sqlQuery = "";
        static int countOfDownloadedImages = 0;

        /* Settings */
        // Creates a .sql file that can be uses as a query to insert the data into a database
        static bool generateSqlQuery = true;
        static string tableName = "RoadSigns";
        // Prints the query into the command line
        static bool printSqlQuery = false;
        static bool printRoadSignData = false;
        static bool downloadImages = true;
        static string imagesRoot = @"images\";
        // Deletes the 'images' folder
        static bool deleteOldImages = true;
        // Optimizes the images using nQuant
        static bool optimizeImages = true;

        static void Main(string[] args)
        {
            Console.OutputEncoding = System.Text.Encoding.UTF8;
            try
            {
                // download each page and dump the content
                var task = MessageLoopWorker.Run(DoWorkAsync,
                    "http://www.vodicak.sk/znacky/kategoria/code/A/",
                    "http://www.vodicak.sk/znacky/kategoria/code/B/",
                    "http://www.vodicak.sk/znacky/kategoria/code/C/",
                    "http://www.vodicak.sk/znacky/kategoria/code/E/",
                    "http://www.vodicak.sk/znacky/kategoria/code/II/",
                    "http://www.vodicak.sk/znacky/kategoria/code/IP/",
                    "http://www.vodicak.sk/znacky/kategoria/code/IS/",
                    "http://www.vodicak.sk/znacky/kategoria/code/O/",
                    "http://www.vodicak.sk/znacky/kategoria/code/P/",
                    "http://www.vodicak.sk/znacky/kategoria/code/S/",
                    "http://www.vodicak.sk/znacky/kategoria/code/SPEC/",
                    "http://www.vodicak.sk/znacky/kategoria/code/V/",
                    "http://www.vodicak.sk/znacky/kategoria/code/Z/");
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
                " - tableName: " + tableName + "\n" +
                " - generateSqlQuery: " + generateSqlQuery + "\n" +
                " - printSqlQuery: " + printSqlQuery + "\n" +
                " - printRoadSignData: " + printRoadSignData + "\n" +
                " - downloadImages: " + downloadImages + "\n" +
                " - deleteOldImages: " + deleteOldImages + "\n");

            using (var wb = new WebBrowser())
            {
                wb.ScriptErrorsSuppressed = true;

                TaskCompletionSource<bool> tcs = null;
                WebBrowserDocumentCompletedEventHandler documentCompletedHandler = (sender, e) =>
                {
                    var targetPath = Regex.Split(mUrl, ".sk")[1];

                    //Console.WriteLine("targetPath: " + targetPath);
                    //Console.WriteLine("sender.Url: " + (sender as WebBrowser).Url.AbsolutePath);

                    // Waits for the page to trully finish loading
                    if ((sender as WebBrowser).Url.AbsolutePath != targetPath)
                        return;

                    tcs.TrySetResult(true);
                };

                // Iterators
                int i = 1;
                var categoryId = 0;

                // navigate to each URL in the list
                foreach (var url in args)
                {
                    mUrl = url.ToString();
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
                    var urlSplit = mUrl.Split('/');
                    var category = urlSplit[urlSplit.Length - 2];

                    Console.WriteLine("===================================");
                    Console.WriteLine("URL: " + mUrl);
                    Console.WriteLine("Category: " + category + " (" + categoryId + ")\n");

                    var container = wb.Document.GetElementById("maincol");
                    var divs = container.GetElementsByTagName("div");

                    var imagesFolder = imagesRoot + category;
                    var imageUrl = "";
                    var imageName = "";
                    var identifier = "";
                    var title = "";
                    var desc = "";

                    // Delete old images in this directory
                    if (deleteOldImages && downloadImages)
                    {
                        Directory.CreateDirectory(imagesFolder);
                        System.IO.DirectoryInfo directoryInfo = new DirectoryInfo(imagesFolder);

                        foreach (FileInfo file in directoryInfo.GetFiles())
                        {
                            file.Delete();
                        }
                        foreach (DirectoryInfo dir in directoryInfo.GetDirectories())
                        {
                            dir.Delete(true);
                        }
                    }


                    foreach (HtmlElement div in divs)
                    {
                        // Get a signs: image, title and description
                        if (div.GetAttribute("className") == "znacky kategoria detail")
                        {
                            imageUrl = div.GetElementsByTagName("img")[0].GetAttribute("src");
                            var withExtension = imageUrl.Split('/');
                            imageName = withExtension[withExtension.Length - 1].Split('.')[0];

                            title = div.GetElementsByTagName("b")[0].InnerHtml;
                            identifier = Regex.Split(title, ": ")[0];
                            title = Regex.Split(title, ": ")[1];

                            if (printRoadSignData)
                            {
                                Console.WriteLine("Identifier: " + identifier);
                                Console.WriteLine("ImagePath: " + imageUrl);
                                Console.WriteLine("Title: " + title);
                            }

                            // Download and save the image
                            if (downloadImages)
                            {
                                WebClient wc = new WebClient();

                                if (optimizeImages)
                                {
                                    var quantizer = new WuQuantizer();

                                    string imagePath = imagesFolder + "\\" + imageName + "-uncompressed.png";
                                    string targetPath = imagesFolder + "\\" + imageName + ".png";
                                    wc.DownloadFile(imageUrl, imagePath);

                                    using (var bitmap = new Bitmap(imagePath))
                                    {
                                        using (var quantized = quantizer.QuantizeImage(bitmap))
                                        {
                                            quantized.Save(targetPath, ImageFormat.Png);
                                        }
                                    }

                                    File.Delete(imagePath);
                                }
                                else
                                {
                                    string imagePath = imagesFolder + "\\" + imageName + ".png";
                                    wc.DownloadFile(imageUrl, imagePath);
                                }

                                countOfDownloadedImages++;
                            }
                        }
                        else if (div.GetAttribute("className") == "modal hide fade")
                        {
                            var descElem = div.GetElementsByTagName("p")[0];
                            desc = descElem.InnerHtml;

                            desc = Regex.Split(desc, "</B>")[1];

                            if (printRoadSignData)
                            {
                                //Console.WriteLine("Description: " + desc);
                                Console.WriteLine("");
                            }

                            // Generate SQL query
                            if (generateSqlQuery)
                            {
                                sqlQuery += "INSERT INTO \"" + tableName + "\" VALUES ('" + 
                                    i + "', '" + category + "', '" + 
                                    identifier + "', '" + 
                                    title.Replace("'", "''") + "', '" + 
                                    imageName + "', '" + 
                                    desc.Replace("'", "''") + "');\n";
                            }

                            i++;
                        }

                    }

                    if (generateSqlQuery && printSqlQuery)
                    {
                        Console.WriteLine("\nsqlQuery for category " + category + ":\n" + sqlQuery);
                    }

                    categoryId++;
                }

                if (generateSqlQuery)
                {
                    sqlQuery = "-- GENERATED WITH RoadSignDownloader v" + version + "\n" + sqlQuery;
                    System.IO.File.WriteAllText(@"RoadSigns.sql", sqlQuery);
                }
            }

            Console.WriteLine("Job well done.");

            if (downloadImages)
            {
                Console.WriteLine("Downloaded " + countOfDownloadedImages + " images.");
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
