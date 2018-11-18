using System.Collections.Generic;
using Microsoft.Azure.CognitiveServices.Vision.ComputerVision.Models;
using Newtonsoft.Json;
using Newtonsoft.Json.Serialization;

namespace ImageRecognition
{
    [JsonObject(NamingStrategyType = typeof(CamelCaseNamingStrategy))]
    public class DataContainer
    {
        public ImageAnalysis Analysis { get; set; }
        public IEnumerable<Line> TextLinesPrinted { get; set; }
        public IEnumerable<Line> TextLinesHandWritten { get; set; }

        public DataContainer(ImageAnalysis analysis, IEnumerable<Line> textLinesPrinted, IEnumerable<Line> textLinesHandwritten)
        {
            Analysis = analysis;
            TextLinesPrinted = textLinesPrinted;
            TextLinesHandWritten = textLinesHandwritten;
        }
    }
}