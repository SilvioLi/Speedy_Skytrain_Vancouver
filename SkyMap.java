package com.speedy.skytrain;
import com.example.speedyskytrain.R;

import uk.co.senab.photoview.PhotoViewAttacher;
import android.app.ActionBar;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;

public class SkyMap extends Activity {

	PhotoViewAttacher attacher;
	ImageView img;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.skytrain_map);
		
		//back button on title
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		img = (ImageView)findViewById(R.id.skytrainmap);
        
        Drawable bitmap = getResources().getDrawable(R.drawable.skytraimap);
        img.setImageDrawable(bitmap);
        attacher = new PhotoViewAttacher(img);
        
	}

	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case android.R.id.home:
	            // app icon in action bar clicked; goto parent activity.
	            this.finish();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
}
