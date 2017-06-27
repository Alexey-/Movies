package com.example.movies.utils;

import com.example.movies.MoviesApplication;

public class DIP {
	
	private static float scale = 0;
	
	private static void init() {
		try {
			scale = MoviesApplication.getInstance().getResources().getDisplayMetrics().density;
		} catch (NullPointerException e) {
			scale = 1; //used for xml editing
		}
	}

	public static int toPx(int dp) {
		if (scale == 0) init();
		return Math.round(dp * scale);
	}
	
	public static int toDp(int px) {
		if (scale == 0) init();
		return Math.round(px / scale);
	}
	
}
