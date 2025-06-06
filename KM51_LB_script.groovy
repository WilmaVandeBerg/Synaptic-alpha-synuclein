// ********************************LBs in KM51 staining**********************************************


// 1. Standard settings 
setImageType('BRIGHTFIELD_H_DAB');
setColorDeconvolutionStains('{"Name" : "H-DAB default", "Stain 1" : "Hematoxylin", "Values 1" : "0.65111 0.70119 0.29049 ", "Stain 2" : "DAB", "Values 2" : "0.26917 0.56824 0.77759 ", "Background" : " 255 255 255 "}');


                // 1 DETECT LEWY (LIKE) BODIES  1
// 2. Detect Lewy- like bodies 
selectAnnotations();
runPlugin('qupath.imagej.detect.cells.WatershedCellDetection', '{"detectionImageBrightfield": "Optical density sum",  "requestedPixelSizeMicrons": 0.5,  "backgroundRadiusMicrons": 25.0,  "medianRadiusMicrons": 1.0,  "sigmaMicrons": 3.0,  "minAreaMicrons": 19.6,  "maxAreaMicrons": 490.9,  "threshold": 0.6,  "maxBackground": 8.0,  "watershedPostProcess": true,  "excludeDAB": false,  "cellExpansionMicrons": 0.0,  "includeNuclei": true,  "smoothBoundaries": true,  "makeMeasurements": true}');

runObjectClassifier("DAB2");
selectObjectsByClassification("Background");
clearSelectedObjects(true);
selectObjects();

// 2.3 Count lewy bodies  ---- **** TO SAVE YOUR OWN DATA, CORRECT THIS FILE LOCATION
saveAnnotationMeasurements('Z:/CNAB/i.frigerio/MJFF project/Raw data/5th batch basic markers/aSyn/aSyn cortex - SCRIPT/Results/')
