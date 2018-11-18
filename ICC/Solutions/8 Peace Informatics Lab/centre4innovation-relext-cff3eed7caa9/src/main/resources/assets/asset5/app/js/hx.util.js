/**
 * General utilities.
 * Arvid Halma
 */

$.hx = $.hx || {}

String.prototype.getHashCode = function() {
    let hash = 0;
    if (this.length === 0) return hash;
    for (let i = 0; i < this.length; i++) {
        hash = this.charCodeAt(i) + ((hash << 5) - hash);
        hash = hash & hash; // Convert to 32bit integer
    }
    return hash;
};

Number.prototype.intToHSL = function() {
    const shortened = this % 360;
    return "hsl(" + shortened + ",60%,50%)";
};

$.hx.stringToColour = function(str) {
    return str.getHashCode().intToHSL()
}

$.hx.csvString = function(rows, separator="\t"){
    let csvContent = ""
    rows.forEach(function(rowArray){
        let row = rowArray.join(separator);
        csvContent += row + "\r\n";
    })
    return csvContent;
}

$.hx.csvDownload = function(rows, separator="\t", filename='data.csv'){
    saveAs(new Blob([$.hx.csvString(rows, separator)], {type: "text/csv;charset=utf-8"}), filename)
}

$.hx.csvParse = function(text, separator="\t"){
    let rows = text.split(/\r?\n/)
    return rows.map(row => row.split(separator));
}

