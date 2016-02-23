package ca.dioo.java.commons;

import java.io.IOException;
import java.io.InputStream;
import java.lang.Math;
import java.util.Arrays;

public class BinaryWithHeaderStream extends InputStream {
	private static final byte[] END_MAGIC = new byte[]{(byte)0xEE, (byte)0x00, (byte)0xFF};

	protected byte[] buf;
	protected int count;
	protected int pos;

	private int searchOffset;
	private int end;
	private InputStream is;
	private byte[] endMagic;


	public BinaryWithHeaderStream() {
		throw new Error("This is not the constructor you are looking for");
	}


	public BinaryWithHeaderStream(InputStream is) {
		this(is, END_MAGIC);
	}


	public BinaryWithHeaderStream(InputStream is, byte[] endMagic) {
		this.is = is;
		buf = new byte[32768];
		count = 0;
		pos = 0;

		searchOffset = 0;
		end = -1;
		this.endMagic = endMagic;
	}


	public int read() throws IOException {
		int ret = _read(1);

		if (ret < 1) {
			return -1;
		}

		pos++;
		return (int)(buf[pos - 1] & 0xFF);
	}


	public int read(byte[] b, int off, int len) throws IOException {
		int ret = _read(len);

		if (ret < 1) {
			return -1;
		}

		System.arraycopy(buf, pos, b, off, ret);
		pos += ret;
		return ret;
	}


	public int available() throws IOException {
		return _readAvailable();
	}


	public byte[] getBuffer() {
		if (end == -1) {
			throw new Error("end not yet found");
		}
		return Arrays.copyOfRange(buf, pos + endMagic.length, count);
	}


	private int _getSafeEnd() {
		if (end > -1) {
			return end;
		} else {
			return searchOffset;
		}
	}


	private int _readAvailable() throws IOException {
		int len = is.available();

		return _read(len);
	}


	private int _read(int nbBytes) throws IOException {
		int nbToRead = Math.min(buf.length - count,
				Math.max(is.available(), nbBytes));

		if (end > -1) {
			if (pos >= end) {
				return -1;
			}
			return Math.min(nbBytes, end - pos);
		}

		assert !(count > buf.length);

		if (count + nbToRead >= buf.length) {
			System.arraycopy(buf, pos, buf, 0, count - pos);
			count -= pos;
			searchOffset -= pos;
			pos = 0;
		}

		int ret = is.read(buf, count, buf.length - count);
		if (ret > 0) {
			count += ret;

			indexOfEndMagic();
			ret = Math.min(ret, _getSafeEnd() - pos);
		}

		if (ret > nbBytes) {
			return nbBytes;
		}
		return ret;
	}


	private int indexOfEndMagic() {
		if (end > -1) {
			return end;
		}

		for (int i = searchOffset; i < buf.length; i++) {
			int j = 0;
			for ( ;
					j < endMagic.length
					&& i + j < buf.length
					&& buf[i + j] == endMagic[j]
					; j++);

			//endMagic found
			if (j == endMagic.length) {
				end = i;
				searchOffset = end;
			} else if (j >= endMagic.length || i + j >= buf.length) {
				//Pass
			} else if (buf[i + j] == endMagic[j]) {
				searchOffset = i;
			}
		}

		return end;
	}
}
