package com.solaredge.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import android.os.Environment;

import com.solaredge.fusion.FusionCode;

public class SerializeUtil {

	public static void serializeObject(String name, Object objec) {
		checkMysoftStage(FusionCode.SERIALIZE_PATH);
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(FusionCode.SERIALIZE_PATH + "/" + name);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(objec);
			oos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Object deserializeObject(String name) {
		Object object = null;
		try {
			FileInputStream fis = new FileInputStream(FusionCode.SERIALIZE_PATH
					+ "/" + name);
			ObjectInputStream ois = new ObjectInputStream(fis);
			object = ois.readObject();
			ois.close();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}

		return object;
	}

	public static void checkMysoftStage(String uri) {
		String dataDir = Environment.getDataDirectory().getAbsolutePath();
		if ((dataDir).equals(uri.substring(0, 5))) {
			File file = new File(uri);
			if (!file.exists()) {
				boolean success = file.mkdir();
				if (!success) {
					return;
				}
			}
		} else {
			String sdcardDir = Environment.getExternalStorageDirectory()
					.getAbsolutePath();
			if (new File(sdcardDir).canRead()) {
				File file = new File(sdcardDir + "/solaredge");
				if (!file.exists()) {
					boolean success = file.mkdir();
					if (!success) {
						return;
					}
				}

				file = new File(uri);
				boolean success = file.mkdir();
				if (!success) {
					return;
				}
			}
		}
	}
}
