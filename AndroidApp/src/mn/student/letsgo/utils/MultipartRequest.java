package mn.student.letsgo.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;

/**
 * A request for making a Multi Part request
 * 
 * @param <T>
 *            Response expected
 */
public class MultipartRequest extends Request<JSONObject> {

	private MultipartEntity entity = new MultipartEntity();

	private static final String FILE_PART_NAME = "file";
	private static final String STRING_PART_NAME = "text";

	private final Response.Listener<JSONObject> mListener;

	public MultipartRequest(String url, Response.ErrorListener errorListener,
			Response.Listener<JSONObject> listener) {
		super(Method.POST, url, errorListener);

		mListener = listener;

	}

	public void addFileUpload(File mFilePart) {
		entity.addPart(FILE_PART_NAME, new FileBody(mFilePart));
	}

//	public void addStringUpload(String key, String value) {
//		try {
//			entity.addPart(STRING_PART_NAME, );
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

//	}

	@Override
	public String getBodyContentType() {
		return entity.getContentType().getValue();
	}

	@Override
	public byte[] getBody() throws AuthFailureError {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			entity.writeTo(bos);
		} catch (IOException e) {
			VolleyLog.e("IOException writing to ByteArrayOutputStream");
		}
		return bos.toByteArray();
	}

	@Override
	protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
		try {
			String jsonString = new String(response.data,
					HttpHeaderParser.parseCharset(response.headers));
			return Response.success(new JSONObject(jsonString),
					HttpHeaderParser.parseCacheHeaders(response));
		} catch (UnsupportedEncodingException e) {
			return Response.error(new ParseError(e));
		} catch (JSONException je) {
			return Response.error(new ParseError(je));
		}
	}

	@Override
	protected void deliverResponse(JSONObject response) {
		// TODO Auto-generated method stub
		mListener.onResponse(response);
	}
}