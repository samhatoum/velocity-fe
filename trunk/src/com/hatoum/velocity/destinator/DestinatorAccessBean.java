package com.hatoum.velocity.destinator;

import com.hatoum.velocity.framework.AccessBeanBase;

public class DestinatorAccessBean extends AccessBeanBase {

	static {
		allProperties.add("roots");
		allProperties.add("currentDirectory");
		allProperties.add("currentFile");
		allProperties.add("directoriesView");
		allProperties.add("filesView");
		allProperties.add("mixedView");
		allProperties.add("currentFileStale");
	}

	public void reset() {
		// TODO Auto-generated method stub
	}
}
