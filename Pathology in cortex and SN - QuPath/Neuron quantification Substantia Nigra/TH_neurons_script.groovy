// ********************************TH neurons in Substantia Nigra**********************************************

// This groovy (QuPath) script quantifies TH positive (dopaminergic) neurons and threads in Substantia Nigra (SN).

// SN is stained for TH (#22941, Immunostar) visualized with Vector SG (#SK4700, Vector) and Fast Red, to avoid a similar staining color as neuromelanin (brown – avoid DAB).


// *******NECESARRY BEFORE RUNNING THE SCRIPT!! : Draw and classify all the ROIS you have drawn to 'parent'**********

// *******you need to use object classifiers (here referred as Nucleus Circularity, Nucleus Fast Red OD range, Cell Fast Red OD std dev), 
// and a pixel classifier (TH threads)


setImageType('BRIGHTFIELD_OTHER');
setColorDeconvolutionStains('{"Name" : "Vector SG grey - Fast red", "Stain 1" : "Vector SG grey", "Values 1" : "0.61528 0.61528 0.49282 ", "Stain 2" : "Fast Red", "Values 2" : "0.24276 0.71226 0.6586 ", "Background" : " 255 255 255 "}');


// Find Neurons in SN. - With or Without neuromelanin, But with TH for sure
print "Finding TH positive neurons ..." 
selectObjectsByClassification("Parent");
// Find all nuclei
runPlugin('qupath.imagej.detect.cells.WatershedCellDetection', '{"detectionImageBrightfield": "Hematoxylin OD",  "requestedPixelSizeMicrons": 1.0,  "backgroundRadiusMicrons": 20.0,  "medianRadiusMicrons": 6.0,  "sigmaMicrons": 3.0,  "minAreaMicrons": 100.0,  "maxAreaMicrons": 9999.0,  "threshold": 0.05,  "maxBackground": 5.0,  "watershedPostProcess": true,  "cellExpansionMicrons": 3.0,  "includeNuclei": true,  "smoothBoundaries": true,  "makeMeasurements": true}');
runObjectClassifier("Area");
// might need to change the background classifier, it takes out too much
selectObjectsByClassification("Background");
clearSelectedObjects(true);
clearSelectedObjects();
runObjectClassifier("Nucleus Circularity");
selectObjectsByClassification(null);
clearSelectedObjects(true);
clearSelectedObjects();
runObjectClassifier("Nucleus Fast Red OD range");
selectObjectsByClassification(null);
clearSelectedObjects(true);
clearSelectedObjects();
// this is good
runObjectClassifier("Cell Fast Red OD std dev");
selectObjectsByClassification(null);
clearSelectedObjects(true);
clearSelectedObjects();
print "...Found them!"

//Save the TH positive neurons 

saveAnnotationMeasurements('Z:/your/folder/THneurons/')

//Make from 'TH neurons' annotations

print "Processing TH neurons..." 
def detectionsTH = getDetectionObjects().findAll{it.getPathClass() == getPathClass("Neurons")}
def newAnnotationsTH = detectionsTH.collect {
    return PathObjects.createAnnotationObject(it.getROI(), it.getPathClass()) 
}
removeObjects(detectionsTH, true)
insertObjects(newAnnotationsTH)

def annotations = getAnnotationObjects()
removeObjects(annotations, false)
addObjects(annotations)

selectObjectsByClassification("Neurons");
mergeSelectedAnnotations()


        
// Substract annotations from ROI

selectObjectsByClassification("Neurons", "Neurons");
runPlugin('qupath.lib.plugins.objects.SplitAnnotationsPlugin', '{}');

resolveHierarchy();

import qupath.lib.roi.* 
import qupath.lib.objects.*

classToSubtract = 'Neurons'
    
def topLevel = getObjects{return it.getLevel()==1 && it.isAnnotation()}
println(topLevel)
for (parent in topLevel){

    def total = []
    def polygons = []
    subtractions = parent.getChildObjects().findAll{it.isAnnotation() }
    println(subtractions)
    for (subtractyBit in subtractions){
        if (subtractyBit instanceof AreaROI){
           subtractionROIs =splitAreaToPolygons(subtractyBit.getROI())
           total.addAll(subtractionROIs[1])
        } else {total.addAll(subtractyBit.getROI())}              
                
    }     
    if (parent instanceof AreaROI){
        polygons = RoiTools.splitAreaToPolygons(parent.getROI())
        total.addAll(polygons[0])
    } else { polygons[1] = parent.getROI()}

            
    def newPolygons = polygons[1].collect {
    updated = it
    for (hole in total)
         updated = RoiTools.combineROIs(updated, hole, RoiTools.CombineOp.SUBTRACT)
         return updated
    }
                // Remove original annotation, add new ones
    annotations = newPolygons.collect {new PathAnnotationObject(updated, parent.getPathClass())}


    addObjects(annotations)

    removeObjects(subtractions, true)
    removeObject(parent, true)
}
print "Done with extracting all neurons from ROIs, Percentage TH treads are being analysed on the ROI now"


// Analyze percentage of TH Threads on ROIs

print "Start process of pixel classifier. Detect all TH threads in ROI" 
selectAnnotations();
addPixelClassifierMeasurements("TH threads", "TH threads")
saveAnnotationMeasurements('Z:/your/folder/THthreads/')
 
 print "Done with whole script"


