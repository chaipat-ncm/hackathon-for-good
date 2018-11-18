using System;
using System.Collections.Concurrent;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Diagnostics;
using System.IO;
using System.Linq;
using System.Threading.Tasks;
using Core.Extensions;
using Microsoft.Azure.CognitiveServices.Vision.ComputerVision;
using Microsoft.Azure.CognitiveServices.Vision.ComputerVision.Models;

namespace ImageRecognition
{
    public class ImageAnalyzer
    {
        private const string path = @"C:\Users\frans\Documents\HackatonForGood\Images";
        private static int NumberOfRequests = 0;
        public static Stopwatch OverallStopwatch;
        
        private static ConcurrentDictionary<int, long> numberToTimeStampDict = new ConcurrentDictionary<int, long>();                
        private const int numberOfCharsInOperationId = 36;

        // Specify the features to return
        private static readonly List<VisualFeatureTypes> features =
            new List<VisualFeatureTypes>()
            {
                VisualFeatureTypes.Categories, VisualFeatureTypes.Description,
                VisualFeatureTypes.Faces, VisualFeatureTypes.ImageType,
                VisualFeatureTypes.Tags, VisualFeatureTypes.Color, VisualFeatureTypes.Adult
            };

        
        public async Task Analyze(ComputerVisionClient computerVision, string fileName)
        {
            try
            {
                var localImagePath = Path.Combine($"{path}", fileName);

                Console.WriteLine($"Image {fileName} being analyzed ...");
                var tHandWritten
                    = await ExtractLocalTextAsync(computerVision, localImagePath, TextRecognitionMode.Handwritten)
                        .ConfigureAwait(false);
                var tPrinted = await ExtractLocalTextAsync(computerVision, localImagePath, TextRecognitionMode.Printed)
                    .ConfigureAwait(false);
                var tAnalyze = await AnalyzeLocalAsync(computerVision, localImagePath).ConfigureAwait(false);

                Console.WriteLine($"Image {fileName} being serialized ...");
                var dataContainer = new DataContainer(tAnalyze, tPrinted, tHandWritten);
                var text = dataContainer.ToJson();
                var writeTask = WriteTextAsync(CreateAnnotationsFileName(fileName), text);
                Task.WhenAll(writeTask).Wait();
                Console.WriteLine($"Image {fileName} done ...");
            }
            catch (Exception ex)
            {
                Console.Out.WriteLine($"Processing failed of file {fileName}");
                Console.Out.Write(ex.Message);
            }
        }
        
        public static string CreateAnnotationsFileName(string filename)
        {
            var file_name = filename.Remove(filename.IndexOf('.'));

            return Path.Combine($"{path}\\Annotations", $"{file_name}.json");
        }
        
        private async Task WriteTextAsync(string path, string text)
        {
            // Set a variable to the My Documents path.

            // Write the text asynchronously to a new file named "WriteTextAsync.txt".
            using (var outputFile = new StreamWriter(path)) {
                await outputFile.WriteAsync(text).ConfigureAwait(false);
            }
        }
        
        private async Task AnalyzeRemoteAsync(
            ComputerVisionClient computerVision, string imageUrl)
        {
            if (!Uri.IsWellFormedUriString(imageUrl, UriKind.Absolute))
            {
                Console.WriteLine(
                    "\nInvalid remoteImageUrl:\n{0} \n", imageUrl);
                return;
            }

            ImageAnalysis analysis =
                await computerVision.AnalyzeImageAsync(imageUrl, features).ConfigureAwait(false);
            DisplayResults(analysis, imageUrl);
        }

        // Analyze a local image
        private async Task<ImageAnalysis> AnalyzeLocalAsync(
            ComputerVisionClient computerVision, string imagePath)
        {
            if (!File.Exists(imagePath))
            {
                Console.WriteLine(
                    "\nUnable to open or read localImagePath:\n{0} \n", imagePath);
                return null;
            }

            using (Stream imageStream = File.OpenRead(imagePath))
            {
                AssesTime();
                var analysis = await computerVision.AnalyzeImageInStreamAsync(
                    imageStream, features).ConfigureAwait(false);
                
//                DisplayResults(analysis, imagePath);
                return analysis;
            }
        }

        // Display the most relevant caption for the image
        private void DisplayResults(ImageAnalysis analysis, string imageUri)
        {
            Console.WriteLine(imageUri);
            Console.WriteLine(analysis.ToJson());
        }
        
        // Recognize text from a local image
        private async Task<IEnumerable<Line>> ExtractLocalTextAsync(
            ComputerVisionClient computerVision, string imagePath, TextRecognitionMode recognitionMode)
        {
            if (!File.Exists(imagePath))
            {
                Console.WriteLine(
                    "\nUnable to open or read localImagePath:\n{0} \n", imagePath);
                return Enumerable.Empty<Line>();
            }
            Console.Out.WriteLine($"Reading Text in mode {recognitionMode} from {imagePath}");
            using (Stream imageStream = File.OpenRead(imagePath))
            {
                // Start the async process to recognize the text
                AssesTime();
                var textHeaders =
                    await computerVision.RecognizeTextInStreamAsync(
                        imageStream, recognitionMode).ConfigureAwait(false);
                

                return await GetTextAsync(computerVision, textHeaders.OperationLocation).ConfigureAwait(false);
            }
        }

        // Retrieve the recognized text
        private async Task<IEnumerable<Line>> GetTextAsync(
            ComputerVisionClient computerVision, string operationLocation)
        {
            // Retrieve the URI where the recognized text will be
            // stored from the Operation-Location header
            string operationId = operationLocation.Substring(
                operationLocation.Length - numberOfCharsInOperationId);

//            Console.WriteLine("\nCalling GetHandwritingRecognitionOperationResultAsync()");
            AssesTime();
            TextOperationResult result =
                await computerVision.GetTextOperationResultAsync(operationId).ConfigureAwait(false);
            
            // Wait for the operation to complete
            int i = 0;
            int maxRetries = 5;
            while ((result.Status == TextOperationStatusCodes.Running ||
                    result.Status == TextOperationStatusCodes.NotStarted) && i++ < maxRetries)
            {
//                Console.WriteLine(
//                    "Server status: {0}, waiting {1} seconds...", result.Status, i);
                await Task.Delay(2000);
                AssesTime();
                result = await computerVision.GetTextOperationResultAsync(operationId).ConfigureAwait(false);
            }

            // Display the results
//            Console.WriteLine();
            var lines = result.RecognitionResult.Lines;
//            foreach (Line line in lines)
//            {
//                Console.WriteLine(line.Text);
//            }
//            Console.WriteLine();
            return lines;
        }

        private void AssesTime()
        {
            var elapsedTime = OverallStopwatch.ElapsedMilliseconds;

            numberToTimeStampDict.TryAdd(NumberOfRequests, elapsedTime);
            var twentyStepsback = NumberOfRequests - 17;
            NumberOfRequests++;
            if (twentyStepsback < 0) return;
            numberToTimeStampDict.TryGetValue(twentyStepsback, out var previousTime);
            var timediff = elapsedTime - previousTime;
            if (timediff < 55000)
            {
                var i = 61000 - (int) (timediff);
                System.Threading.Thread.Sleep(i);
            }
            

            Console.Out.WriteLine($"Request number: {NumberOfRequests} Timediff {timediff} s");
        }
    }
}