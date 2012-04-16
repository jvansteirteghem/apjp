/*
APJP, A PHP/JAVA PROXY
Copyright (C) 2009-2011 Jeroen Van Steirteghem

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with this program; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
*/

package APJP.ANDROID;

import iaik.security.provider.IAIK;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.security.Security;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainUI1 extends Activity
{
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		System.setProperty("sun.net.http.allowRestrictedHeaders", "true");
		
		Security.addProvider(new IAIK());
		
		setContentView(R.layout.main_ui1);
		
		final Button startButton = (Button) findViewById(R.id.startButton);
		final Button stopButton = (Button) findViewById(R.id.stopButton);
		final Button preferencesButton = (Button) findViewById(R.id.preferencesButton);
		final TextView outputTextView = (TextView) findViewById(R.id.outputTextView);
		
		final Handler handler = new Handler();
		
		final OutputStream outputStream = new OutputStream()
		{
			public void write(final int b) throws IOException  
			{
				handler.post
				(
					new Runnable()
					{
						public void run()
						{
							outputTextView.append(new String(new char[] {(char) b}));
						}
					}
				);
			}
			
			public void write(final byte[] b, final int off, final int len) throws IOException
			{
				handler.post
				(
					new Runnable()
					{
						public void run()
						{
							outputTextView.append(new String(b, off, len));
						}
					}
				);
			}
		};
		
		final PrintStream printStream = new PrintStream(outputStream, true);
		
		System.setOut(printStream);
		
		startButton.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				outputTextView.setText("");
				
				startService(new Intent(getBaseContext(), Main.class));
			}
		});
		
		stopButton.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				outputTextView.setText("");
				
				stopService(new Intent(getBaseContext(), Main.class));
			}
		});
		
		preferencesButton.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				Intent intent = new Intent(getBaseContext(), MainUI2.class);
				startActivity(intent);
			}
		});
	}
}
