package org.sung.sqltranslator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractAnalizer implements Analizer{
	
	@Override
	public void analize(String dir) {
		
		File rootDir = new File(dir);//"D:\\ClearCase"
		
		List<File> files = findSubFiles(rootDir);

		for(File f : files){
			processFile(f);
		}
		
	}
	
	private List<File> findSubFiles(File dir){
		List<File> rslt = new ArrayList<File>();
	    File[] files = dir.listFiles();

	    if(!dir.isDirectory() && isInteresting(dir)){
	    	rslt.add(dir);
	    	return rslt;
	    }
	    
	    for (int x=0;x<files.length;++x)
	    {
	    		    	
	        if (files[x].isDirectory()){
	        	List<File> srst = findSubFiles(files[x]);
	        	if(srst.size() > 0)
	        		rslt.addAll(srst);
	        }else{
	        		
	        	if (isInteresting(files[x])){	       
	        		System.out.println("target file : " + files[x].getName());
	        	    	rslt.add(files[x]);
	        	}
	        }
	    }
	    System.out.println(rslt.size());
	    return rslt;
	}

	public abstract boolean isInteresting(File f);
	
	public abstract void processFile(File f);
	
}