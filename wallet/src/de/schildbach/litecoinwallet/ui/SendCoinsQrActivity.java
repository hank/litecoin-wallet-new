/*
 * Copyright 2013 the original author or authors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.schildbach.litecoinwallet.ui;

import java.math.BigInteger;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;

import com.google.litecoin.core.Address;
import com.google.litecoin.core.Transaction;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentIntegratorSupportV4;
import com.google.zxing.integration.android.IntentResult;
import de.schildbach.litecoinwallet.ui.InputParser.StringInputParser;

/**
 * @author Andreas Schildbach
 */
public final class SendCoinsQrActivity extends AbstractOnDemandServiceActivity
{
	private static final int REQUEST_CODE_SCAN = 0;

	@Override
	protected void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		//startActivityForResult(new Intent(this, ScanActivity.class), REQUEST_CODE_SCAN);
        IntentIntegrator integratorSupportV4 = new IntentIntegrator(this);
	}

	@Override
	public void onActivityResult(final int requestCode, final int resultCode, final Intent intent)
	{
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);

		if (result != null)
		{
			final String input = result.getContents();
			new StringInputParser(input)
			{
				@Override
				protected void bitcoinRequest(final Address address, final String addressLabel, final BigInteger amount, final String bluetoothMac)
				{
					SendCoinsActivity
							.start(SendCoinsQrActivity.this, address != null ? address.toString() : null, addressLabel, amount, bluetoothMac);

					SendCoinsQrActivity.this.finish();
				}

				@Override
				protected void directTransaction(final Transaction transaction)
				{
					processDirectTransaction(transaction);

					SendCoinsQrActivity.this.finish();
				}

				@Override
				protected void error(final int messageResId, final Object... messageArgs)
				{
					dialog(SendCoinsQrActivity.this, dismissListener, 0, messageResId, messageArgs);
				}

				private final OnClickListener dismissListener = new OnClickListener()
				{
					@Override
					public void onClick(final DialogInterface dialog, final int which)
					{
						SendCoinsQrActivity.this.finish();
					}
				};
			}.parse();
		}
		else
		{
			finish();
		}
	}
}
