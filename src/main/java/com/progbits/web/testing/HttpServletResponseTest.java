package com.progbits.web.testing;

import com.progbits.api.model.ApiObject;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Locale;

/**
 *
 * @author scarr
 */
public class HttpServletResponseTest implements HttpServletResponse {

	private int status = 200;
	private transient String characterEncoding = "UTF-8";
	private ApiObject headers = new ApiObject();
	private ByteArrayOutputStream out = new ByteArrayOutputStream();
	private BufferedOutputStream buffOut = new BufferedOutputStream(out);
	private PrintWriter printWriter;
	private ServletOutputStream servletOutputStream;

	@Override
	public void addCookie(Cookie cookie) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public boolean containsHeader(String name) {
		return headers.containsKey(name);
	}

	@Override
	public String encodeURL(String string) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public String encodeRedirectURL(String string) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public String encodeUrl(String string) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public String encodeRedirectUrl(String string) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void sendError(int i, String string) throws IOException {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void sendError(int i) throws IOException {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void sendRedirect(String string) throws IOException {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void setDateHeader(String string, long l) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void addDateHeader(String string, long l) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void setHeader(String name, String value) {
		headers.setString(name, value);
	}

	@Override
	public void addHeader(String name, String value) {
		headers.setString(name, value);
	}

	@Override
	public void setIntHeader(String name, int value) {
		headers.setInteger(name, value);
	}

	@Override
	public void addIntHeader(String name, int value) {
		headers.setInteger(name, value);
	}

	@Override
	public void setStatus(int i) {
		status = i;
	}

	@Override
	public void setStatus(int i, String string) {
		status = i;
	}

	@Override
	public int getStatus() {
		return status;
	}

	@Override
	public String getHeader(String name) {
		if (headers.getType(name) == ApiObject.TYPE_STRINGARRAY) {
			return headers.getStringArray(name).get(0);
		} else {
			return headers.getString(name);
		}
	}

	@Override
	public Collection<String> getHeaders(String name) {
		return headers.getStringArray(name);
	}

	@Override
	public Collection<String> getHeaderNames() {
		return headers.keySet();
	}

	@Override
	public String getCharacterEncoding() {
		return characterEncoding;
	}

	@Override
	public String getContentType() {
		return headers.getString("Content-Type");
	}

	@Override
	public ServletOutputStream getOutputStream() throws IOException {
		if (servletOutputStream == null) {
			servletOutputStream = new ServletOutputStream() {
				@Override
				public boolean isReady() {
					return true;
				}

				@Override
				public void setWriteListener(WriteListener wl) {
					throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
				}

				@Override
				public void write(int i) throws IOException {
					buffOut.write(i);
				}

				@Override
				public void write(byte[] b) throws IOException {
					buffOut.write(b);
				}

				@Override
				public void write(byte[] b, int off, int len) throws IOException {
					buffOut.write(b, off, len);
				}

			};
		}

		return servletOutputStream;
	}

	@Override
	public PrintWriter getWriter() throws IOException {
		if (printWriter == null) {
			printWriter = new PrintWriter(out, false, Charset.forName(characterEncoding));
		}

		return printWriter;
	}

	@Override
	public void setCharacterEncoding(String characterEncoding) {
		this.characterEncoding = characterEncoding;
	}

	@Override
	public void setContentLength(int contentLength) {
		headers.setLong("Content-Length", (long) contentLength);
	}

	@Override
	public void setContentLengthLong(long contentLength
	) {
		headers.setLong("Content-Length", contentLength);
	}

	@Override
	public void setContentType(String contentType) {
		headers.setString("Content-Type", contentType);
	}

	@Override
	public void setBufferSize(int i) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public int getBufferSize() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void flushBuffer() throws IOException {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void resetBuffer() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public boolean isCommitted() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void reset() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void setLocale(Locale locale) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public Locale getLocale() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

}
