package com.progbits.web;

import com.progbits.api.exception.ApiException;
import com.progbits.api.model.ApiObject;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Base64;
import java.util.Enumeration;
import java.util.stream.Collectors;

/**
 *
 * @author scarr
 */
public class WebUtils {

	private static final DateTimeFormatter _dateHeader = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss 'GMT'");

	/**
	 * Recreates the HTTP Header with the Headers found in the HTTP Request.
	 *
	 * @param req Request Object to pull information from.
	 *
	 * @return String with Headers in standard HTTP Format.
	 */
	public static String getHttpHeaders(HttpServletRequest req) {
		Enumeration<String> eHeaders = req.getHeaderNames();

		StringBuilder sb = new StringBuilder();

		while (eHeaders.hasMoreElements()) {
			String sHead = eHeaders.nextElement();

			if (sb.length() > 0) {
				sb.append("\r\n");
			}

			sb.append(sHead).append(": ").append(req.getHeader(sHead));
		}

		return sb.toString();
	}

	/**
	 * Pull the Body from an HTTP Request. Returns the Body as a String, with
	 * optional Encoding.
	 *
	 * @param req HTTP Request to pull the body from
	 * @return String of the Body
	 *
	 * @throws IOException Exception that occurs during processing.
	 */
	public static String getReqBody(HttpServletRequest req) throws IOException {
		String charEnc = req.getCharacterEncoding();

		if (charEnc == null) {
			charEnc = "UTF-8";
		}

		return req.getReader().lines().collect(Collectors.joining("\n"));
	}

	/**
	 * Pulls the Parameters, and Lower Cases all the key names
	 *
	 * @param req Request to pull the parameters from
	 *
	 * @return ApiObject name value pairs of the Headers found
	 */
	public static ApiObject pullReqParams(HttpServletRequest req) {
		ApiObject apiRet = new ApiObject();

		Enumeration keys = req.getParameterNames();

		while (keys.hasMoreElements()) {
			String key = (String) keys.nextElement();

			//To retrieve a single value
			apiRet.put(key.toLowerCase(), req.getParameter(key));
		}

		// Sets ItemId to the Item from a URL.
		// Assuming format: /ebook/items/13461341412345
		if (req.getRequestURI().contains("items")) {
			apiRet.put("itemid", req.getRequestURI().substring(req.getRequestURI().lastIndexOf("/") + 1));
		}

		return apiRet;
	}

	/**
	 * Pulls the Parameters, and Lower Cases all the key names
	 *
	 * @param req Request to pull the parameters from
	 *
	 * @return ApiObject Parameters in Name Value
	 */
	public static ApiObject pullReqParamValues(HttpServletRequest req) {
		ApiObject objRet = new ApiObject();

		Enumeration keys = req.getParameterNames();

		while (keys.hasMoreElements()) {
			String key = (String) keys.nextElement();

			//To retrieve a single value
			objRet.put(key.toLowerCase(), req.getParameterValues(key));
		}

		return objRet;
	}

	/**
	 * Pulls the Parameters, and Lower Cases all the key names
	 *
	 * @param req Request to pull the parameters from
	 * @param itemIdPath Path to pull an itemid from, we will pull the last part
	 * of the url
	 *
	 * @return ApiObject Parameters in Name Value, itemid will contain the final
	 * part of url
	 */
	public static ApiObject pullReqParamValues(HttpServletRequest req, String itemIdPath) {
		ApiObject objRet = new ApiObject();

		Enumeration keys = req.getParameterNames();

		while (keys.hasMoreElements()) {
			String key = (String) keys.nextElement();

			//To retrieve a single value
			objRet.put(key.toLowerCase(), req.getParameterValues(key));
		}

		// Sets ItemId to the Item from a URL.
		// Assuming format: /ebook/items/13461341412345
		if (req.getRequestURI().contains(itemIdPath)) {
			objRet.put("itemid", new String[]{req.getRequestURI().substring(req.getRequestURI().lastIndexOf("/") + 1)});
		}

		return objRet;
	}

	/**
	 * This function pulls the Parameters from a Request that DOES NOT have form
	 * encoding content type turned on. It pulls the request body, and parses
	 * manually.
	 *
	 * NOTE: All Params are lowercase, to make it easier to check parameters.
	 *
	 * @param req Http Request to pull the information from
	 *
	 * @throws ApiException
	 * @throws IOException
	 */
	public static ApiObject pullBadPost(HttpServletRequest req) throws IOException, ApiException {
		ApiObject objRet = new ApiObject();

		String strBody = getReqBody(req);

		// Pull All Params from Get Line
		objRet.putAll(pullReqParams(req));

		String[] sParams = strBody.split("&");

		if (sParams != null) {
			for (String p : sParams) {
				String[] cp = p.split("=");

				if (cp.length == 1) {
					objRet.put(URLDecoder.decode(cp[0], Charset.forName("UTF-8")).toLowerCase(), "");
				} else {
					objRet.put(URLDecoder.decode(cp[0], Charset.forName("UTF-8")).toLowerCase(),
							URLDecoder.decode(cp[1], Charset.forName("UTF-8")));
				}

			}
		}

		return objRet;
	}

	public static Charset getUtf8Charset() {
		return Charset.forName("UTF-8");
	}

	public static ApiObject pullParams(String methodType, HttpServletRequest req) throws Exception {
		ApiObject objRet = new ApiObject();

		String contentType = req.getHeader("content-type");
		if (contentType == null) {
			contentType = "";
		}

		if (methodType.equalsIgnoreCase("post")
				&& !contentType.contains("application/x-www-form-urlencoded")) {
			objRet.putAll(pullBadPost(req));
		} else {
			objRet.putAll(pullReqParams(req));
		}

		return objRet;
	}

	/**
	 * Return the IP Address from X-Forwarded-For header or RemoteAddr.
	 *
	 * @param req
	 * @return
	 */
	public static String getIpAddress(HttpServletRequest req) {
		String strRet = req.getRemoteAddr();

		String strTest = req.getHeader("X-Forwarded-For");

		try {
			if (strTest != null) {
				strRet = strTest;
			} else {
				strTest = req.getHeader("Forwarded");

				if (strTest != null) {
					int iLoc1 = strTest.indexOf("for");

					if (iLoc1 > -1) {
						int iLoc2 = strTest.indexOf(";", iLoc1);
						if (iLoc2 > -1) {
							strRet = strTest.substring(iLoc1 + 4, iLoc2 - 1);
						} else {
							strRet = strTest.substring(iLoc1 + 4);
						}
					}
				}
			}
		} catch (Exception ex) {
			// Don't care if an exception occured
		}

		return strRet;
	}

	/**
	 * Pull the Authorization Header from an HTTP Request and parse the result
	 *
	 * @param req Http Request to Parse the Authorization Header
	 * @return NULL if Header not found. ApiObject with following fields.
	 * AuthType: BASIC, UserName, Password Or NULL if it doesn't exist
	 *
	 */
	public static ApiObject parseAuthorization(HttpServletRequest req) throws Exception {
		String auth = req.getHeader("Authorization");

		if (auth == null) {
			return null;
		} else {
			ApiObject retObj = new ApiObject();
			String[] splitAuth = auth.split(" ", 2);

			if (splitAuth.length == 1) {
				retObj.setString("AuthType", "BASIC");
				retObj.setString("AuthData", splitAuth[0]);

				if (retObj.getString("AuthData") != null && "BASIC".equalsIgnoreCase(retObj.getString("AuthData"))) {
					// We have an invalid Authorization Object.
					return retObj;
				}
			} else {
				retObj.setString("AuthType", splitAuth[0].toUpperCase());
				retObj.setString("AuthData", splitAuth[1]);
			}

			if ("BASIC".equals(retObj.getString("AuthType"))) {
				String sAuth = null;

				if (retObj.getString("AuthData") != null && !retObj.getString("AuthData").isEmpty()) {
					try {
						sAuth = Arrays.toString(Base64.getDecoder().decode(retObj.getString("AuthData")));

						String[] elements = sAuth.split(":");

						if (sAuth.length() > 1) {
							retObj.setString("UserName", elements[0]);
							retObj.setString("Password", elements[1]);

						} else {
							retObj.setString("UserName", elements[0]);
						}
					} catch (Exception ex) {
						throw new Exception("Invalid Authorization Header: " + auth, ex);
					}
				} else {
					throw new Exception("Invalid Authorization Header: " + auth, null);
				}
			}

			return retObj;
		}
	}

	public static ApiObject pullHeaders(HttpServletRequest req) {
		Enumeration<String> eHdr = req.getHeaderNames();
		ApiObject retObj = new ApiObject();

		while (eHdr.hasMoreElements()) {
			String hdr = eHdr.nextElement();

			retObj.put(hdr, req.getHeader(hdr));
		}

		return retObj;
	}

	/**
	 * Return an Integer from a Http Parameter List.
	 *
	 * @param req HttpServletRequest to test the parameter.
	 * @param param Parameter name to pull.
	 *
	 * @return Integer or null if not found
	 */
	public static Integer pullIntegerParam(HttpServletRequest req, String param) {
		Integer iRet = null;

		try {
			iRet = Integer.parseInt(req.getParameter(param));
		} catch (Exception ex) {

		}

		return iRet;
	}

	public static ApiObject parseDataTableParams(HttpServletRequest req) throws Exception {
		ApiObject objRet = new ApiObject();

		Enumeration<String> enumParams = req.getParameterNames();

		while (enumParams.hasMoreElements()) {
			String strParam = enumParams.nextElement();

			String[] sLevels = strParam.replace("]", "").split("\\[");

			if (sLevels.length > 1) {
				if (isNumeric(sLevels[1])) {
					Integer iCount = Integer.parseInt(sLevels[1]);

					if (objRet.getList(sLevels[0]) == null) {
						objRet.createList(sLevels[0]);
					}

					Integer iMakeUp = (iCount + 1) - objRet.getList(sLevels[0]).size();

					if (iMakeUp > 0) {
						for (int x = 0; x < iMakeUp; x++) {
							objRet.getList(sLevels[0]).add(new ApiObject());
						}
					}

					if (sLevels.length > 4) {
						// No clue
					} else if (sLevels.length == 4) {
						objRet.getList(sLevels[0])
								.get(iCount)
								.setObject(sLevels[2], new ApiObject());
						objRet.getList(sLevels[0]).get(iCount)
								.getObject(sLevels[2])
								.setString(sLevels[3],
										req.getParameter(strParam));
					} else if (sLevels.length == 3) {
						objRet.getList(sLevels[0]).get(iCount)
								.setString(sLevels[2],
										req.getParameter(strParam));
					}
				} else {
					if (objRet.getObject(sLevels[0]) == null) {
						objRet.setObject(sLevels[0], new ApiObject());
					}

					objRet.getObject(sLevels[0]).setString(sLevels[1], req.getParameter(strParam));
				}
			} else {
				objRet.setString(sLevels[0], req.getParameter(strParam));
			}
		}

		return objRet;
	}

	public static boolean isNumeric(String inputData) {
		NumberFormat formatter = NumberFormat.getInstance();
		ParsePosition pos = new ParsePosition(0);
		formatter.parse(inputData, pos);
		return inputData.length() == pos.getIndex();
	}

	/**
	 * Send a file from the Classpath or a local filesystem
	 * 
	 * @param setup ServletSetup with Class Loader and Basepath set
	 * @param aliasPath The Servlet path where this Servlet resides in Context
	 * @param req
	 * @param resp
	 * @throws ServletException
	 * @throws IOException 
	 */
	public static void sendFile(ServletSetup setup, String aliasPath, HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String fileName = pullPath(req.getRequestURI(), aliasPath, setup.getBasePath());
		fileName = URLDecoder.decode(fileName, "UTF-8");

		URL url;

		if (setup.getLoader() != null) {
			url = setup.getLoader().getResource(fileName);
		} else if (setup.getBasePath() != null) {
			url = new File(setup.getBasePath() + fileName).toURI().toURL();
		} else {
			throw new ServletException("BasePath or Loader MUST be set");
		}

		URLConnection conn = url.openConnection();

		resp.setContentType(setup.getContext().getMimeType(fileName));
		resp.setContentLength(conn.getContentLength());
		resp.setHeader("Cache-Control", "max-age=" + setup.getCacheTime());

		if (conn.getLastModified() > 0L) {
			Instant lastModified = Instant.ofEpochMilli(conn.getLastModified());
			OffsetDateTime lastModifedDate = OffsetDateTime.ofInstant(lastModified, ZoneId.of("UTC"));
			resp.setHeader("Last-Modified", _dateHeader.format(lastModifedDate));
		}

		if ("HEAD".equalsIgnoreCase(req.getMethod())) {
			// HEAD does not need content
		} else {
			writeFile(conn.getInputStream(), resp.getOutputStream());
		}
	}

	public static void writeFile(InputStream is, OutputStream out) throws IOException {
		byte[] buffer = new byte[4096]; // tweaking this number may increase performance  
		int len;

		while ((len = is.read(buffer)) != -1) {
			out.write(buffer, 0, len);
		}

		out.flush();
	}

	public static String pullPath(String path, String aliasPath, String basePath) {
		String strRet = path.substring(aliasPath.length());

		strRet = basePath + strRet;

		return strRet;
	}

	/**
	 * Returns a UrlEntry object that skips over the context in the url
	 *
	 * @param req
	 *
	 * @return
	 */
	public static UrlEntry urlEntry(HttpServletRequest req) {
		UrlEntry urlEntry = new UrlEntry();

		urlEntry.setCurrUrl(req.getRequestURI());

		urlEntry.chompUrl();
		urlEntry.chompUrl();

		return urlEntry;
	}
}
