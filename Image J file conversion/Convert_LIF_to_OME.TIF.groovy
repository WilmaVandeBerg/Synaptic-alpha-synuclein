// After confocal imaging of the putamen, we had one .LIFF file per case containing 6 images (4 acquired in the anterior cingulate cortex -ACC-, and 2 in he prefrontal cortex -PFC-). The goal of this script was to extract these 6 images, rename them according case number and location (ACC1, ACC2, ACC3, ACC4, PFC1, PFC2) and convert them into .OME.TIF format, which is supported by Huygens Professional.

import ij.CompositeImage;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.io.OpenDialog;
import ij.plugin.PlugIn;
import ij.process.ImageProcessor;
import ij.process.LUT;
import java.io.IOException;
import loci.formats.ChannelSeparator;
import loci.formats.FormatException;
import loci.formats.IFormatReader;
import loci.plugins.util.ImageProcessorReader;
import loci.plugins.util.LociPrefs;
import ij.WindowManager;
import ij.plugin.ZProjector
import ij.*;
import ij.process.*;
import ij.gui.*;
import ij.measure.Calibration;
import ij.plugin.HyperStackReducer;
import java.awt.*;
import java.util.Vector;
import ij.plugin.ChannelSplitter;
import sc.fiji.coloc.Coloc_2;
import java.io.File;
import ij.plugin.ImageCalculator;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;



String folder_name_base = "Z:/your/folder/";
String folder_name_images = "/MyResults’";
String folder_name_result = "OME.TIF";



ImagePlus imp1 = new ImagePlus();
ImagePlus imp2 = new ImagePlus();
ImageCalculator ic = new ImageCalculator();
String testname = "";
String outname = "";
String outputfolder = "";
String filename = "";
int ifile = 0;

outputfolder = folder_name_base+"/"+folder_name_result;


File folder_images = new File(folder_name_base+"/"+folder_name_images);
File[] files_images = folder_images.listFiles();
int filecount = files_images.length;


for (ifile=0; ifile<filecount; ifile++) {
	testname = files_images[ifile].getName();
	outname = testname.substring(0,testname.length()-4);
	testname = testname.substring(testname.length()-3,testname.length());
	println(testname);
	if (testname == "lif") {
		filename = folder_name_base+"/"+folder_name_images+"/"+files_images[ifile].getName();
		IJ.run("Bio-Formats", "open="+filename+" autoscale color_mode=Composite rois_import=[ROI manager] view=Hyperstack stack_order=XYCZT series_1"); 
		imp1 = WindowManager.getCurrentImage();

		imp1.show();
		IJ.run(imp1, "OME-TIFF...", "save="+outputfolder+"/"+outname+"_ACC1.ome.tif export compression=Uncompressed");
		imp1.changes = false;
		imp1.setIgnoreFlush(true);
		imp1.close();
        
        IJ.run("Bio-Formats", "open="+filename+" autoscale color_mode=Composite rois_import=[ROI manager] view=Hyperstack stack_order=XYCZT series_2"); 
		imp1 = WindowManager.getCurrentImage();
		imp1.show();
		IJ.run(imp1, "OME-TIFF...", "save="+outputfolder+"/"+outname+"_ACC2.ome.tif export compression=Uncompressed");
		imp1.changes = false;
		imp1.setIgnoreFlush(true);
		imp1.close();
		
        IJ.run("Bio-Formats", "open="+filename+" autoscale color_mode=Composite rois_import=[ROI manager] view=Hyperstack stack_order=XYCZT series_3"); 
		imp1 = WindowManager.getCurrentImage();
		imp1.show();
		IJ.run(imp1, "OME-TIFF...", "save="+outputfolder+"/"+outname+"_ACC3.ome.tif export compression=Uncompressed");
		imp1.changes = false;
		imp1.setIgnoreFlush(true);
		imp1.close();
		
        IJ.run("Bio-Formats", "open="+filename+" autoscale color_mode=Composite rois_import=[ROI manager] view=Hyperstack stack_order=XYCZT series_4"); 
		imp1 = WindowManager.getCurrentImage();
		imp1.show();
		IJ.run(imp1, "OME-TIFF...", "save="+outputfolder+"/"+outname+"_ACC4.ome.tif export compression=Uncompressed");
		imp1.changes = false;
		imp1.setIgnoreFlush(true);
		imp1.close();
		
        IJ.run("Bio-Formats", "open="+filename+" autoscale color_mode=Composite rois_import=[ROI manager] view=Hyperstack stack_order=XYCZT series_5"); 
		imp1 = WindowManager.getCurrentImage();
		imp1.show();
		IJ.run(imp1, "OME-TIFF...", "save="+outputfolder+"/"+outname+"_PFC1.ome.tif export compression=Uncompressed");
		imp1.changes = false;
		imp1.setIgnoreFlush(true);
		imp1.close();
		
        IJ.run("Bio-Formats", "open="+filename+" autoscale color_mode=Composite rois_import=[ROI manager] view=Hyperstack stack_order=XYCZT series_6"); 
		imp1 = WindowManager.getCurrentImage();
		imp1.show();
		IJ.run(imp1, "OME-TIFF...", "save="+outputfolder+"/"+outname+"_PFC2.ome.tif export compression=Uncompressed");
		imp1.changes = false;
		imp1.setIgnoreFlush(true);
		imp1.close();

	}

}

