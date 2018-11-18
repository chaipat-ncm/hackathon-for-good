using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.IO;
using System.Linq;
using System.Net.Http;
using System.Threading.Tasks;
using System.Timers;
using Core.Extensions;
using Microsoft.Azure.CognitiveServices.Vision.ComputerVision;
using Microsoft.Azure.CognitiveServices.Vision.ComputerVision.Models;

namespace ImageRecognition
{
    class Program
    {
        // subscriptionKey = "0123456789abcdef0123456789ABCDEF"
        private const string subscriptionKey = "786035fa1cf0461a9b53445ed1c0307b";
        private const string path = @"C:\Users\frans\Documents\HackatonForGood\Images";

        private static readonly string fileName = "img_102_.jpg";


        

        static void Main(string[] args)
        {
            ComputerVisionClient computerVision = new ComputerVisionClient(
                new ApiKeyServiceClientCredentials(subscriptionKey),
                new DelegatingHandler[] { });

            // You must use the same region as you used to get your subscription
            // keys. For example, if you got your subscription keys from westus,
            // replace "westcentralus" with "westus".
            //
            // Free trial subscription keys are generated in the westcentralus
            // region. If you use a free trial subscription key, you shouldn't
            // need to change the region.

            // Specify the Azure region
            computerVision.Endpoint = "https://westcentralus.api.cognitive.microsoft.com";
//            var counter = 1;
//            var taskList = new List<Task>();
//
            ImageAnalyzer.OverallStopwatch = Stopwatch.StartNew();
//            var numberOfRequests = 0;
//            
//            foreach (var listFileName in ListFileNames())
//            {
//                var stopwatch = Stopwatch.StartNew();
////                Console.Out.WriteLine($"Count: {counter}");
//                var annotationsFileName = ImageAnalyzer.CreateAnnotationsFileName(listFileName);
//                counter++;
//                if (File.Exists(annotationsFileName))
//                {
////                    Console.WriteLine($"Image {fileName} was already processed...");
//                    continue;
//                }
////                Console.WriteLine($"Image {listFileName} was not processed...");
////                continue;
//                var imageAnalyzer = new ImageAnalyzer();
//                Task.WhenAll(imageAnalyzer.Analyze(computerVision, listFileName)).Wait();
//                var timePassed = stopwatch.ElapsedMilliseconds;
//                Console.Out.WriteLine($"Time elapsed: {timePassed/1000} s");
//                if (timePassed < 10000)
//                {
//                    var waitFor = 10000 - timePassed;
//                    System.Threading.Thread.Sleep((int) waitFor);
//                }
////                System.Threading.Thread.Sleep(10000);
//
//
//            }
//
//            Task.WhenAll(taskList).Wait(5000);
            
            var imageAnalyzer = new ImageAnalyzer();
            Task.WhenAll(imageAnalyzer.Analyze(computerVision, fileName)).Wait(1000);
            
            Console.WriteLine("Press ENTER to exit");
            Console.ReadLine();
        }

        private static IEnumerable<string> ListFileNames()
        {
            DirectoryInfo d = new DirectoryInfo(path);//Assuming Test is your Folder
            FileInfo[] Files = d.GetFiles("img_*"); //Getting Text files
            return Files.Select(file => file.Name);
        }



       

        

    }
}