// ********************************LBs in pSer129 staining in Substantia Nigra**********************************************

// This groovy (QuPath) script quantifies Lewy bodies (LBs) and alpha-synuclein pathology (non LB, like Lewy Neurites) in Substantia Nigra (SN).

// SN is stained for pSer129 alpha-synuclein (#ab51253, Abcam) visualized with Vector SG (#SK4700, Vector) and Fast Red, to avoid a similar staining color as neuromelanin (brown – avoid DAB).

// *******NECESARRY BEFORE RUNNING THE SCRIPT!! : Draw and classify all the ROIS you have drawn to 'parent'**********
// *******you need use object classifiers (Nucleus min caliper, Nucleus Hematoxylin OD min), and pixel classifier (aSyn Pathology)


// Strategy for this script: 
//    1.  Detect neurons: make from this cell object detection; annotations, we will use this further down the script. 
//    2.  Detect Lewy bodies
//    3.  Include only intracellular Lewy bodies
//    4.  Run a Pixel classifier over the ROIs for Lewy neurite load


setImageType('BRIGHTFIELD_H_E');
setColorDeconvolutionStains('{"Name" : "H&E default", "Stain 1" : "Hematoxylin", "Values 1" : "0.65111 0.70119 0.29049 ", "Stain 2" : "Eosin", "Values 2" : "0.2159 0.8012 0.5581 ", "Background" : " 255 255 255 "}');

selectObjectsByClassification("Parent");
runPlugin('qupath.imagej.detect.cells.WatershedCellDetection', '{"detectionImageBrightfield": "Hematoxylin OD",  "requestedPixelSizeMicrons": 1.0,  "backgroundRadiusMicrons": 0.0,  "medianRadiusMicrons": 4.0,  "sigmaMicrons": 3.5,  "minAreaMicrons": 28.27,  "maxAreaMicrons": 99999.0,  "threshold": 0.2,  "maxBackground": 5.0,  "watershedPostProcess": true,  "cellExpansionMicrons": 1.0,  "includeNuclei": true,  "smoothBoundaries": true,  "makeMeasurements": true}');
runObjectClassifier("Nucleus min caliper");
selectObjectsByClassification("Extracellular Lewy Body");
selectObjectsByClassification("Background");
clearSelectedObjects(true);
clearSelectedObjects();
runObjectClassifier("Nucleus Hematoxylin OD min");
selectObjectsByClassification("Background");
clearSelectedObjects(true);
clearSelectedObjects();

saveAnnotationMeasurements('Z:/your/folder/LBcount/')
fireHierarchyUpdate()

// ************** Run a Pixel classifier over the ROIs  (Analyse asyn Pathology load)**************

def detectionsLewy = getDetectionObjects().findAll{it.getPathClass() == getPathClass("Lewy Body")}
def newAnnotationsLewy = detectionsLewy.collect {
    return PathObjects.createAnnotationObject(it.getROI(), it.getPathClass()) 
}
removeObjects(detectionsLewy, true)
insertObjects(newAnnotationsLewy)

print "done with Intracellular Lewy Bodies" 

          
// **************  Substract annotations from ROI**************

resolveHierarchy();

import qupath.lib.roi.*
import qupath.lib.objects.*

classToSubtract = 'Lewy Body'
    
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

print "Done with substracting intracellular Lewy Bodies from ROI"

selectAnnotations();
addPixelClassifierMeasurements("aSyn Pathology", "aSyn Pathology")
saveAnnotationMeasurements('Z:/your/folder/aSyn pathology/')
 
print "Done with script"

