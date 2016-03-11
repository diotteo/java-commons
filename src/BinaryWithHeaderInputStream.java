package ca.dioo.java.commons;

import java.io.IOException;
import java.io.InputStream;
import java.lang.Math;
import java.util.Arrays;

public class BinaryWithHeaderInputStream {
	private static final byte[] END_MAGIC = new byte[]{(byte)0xEE, (byte)0x00, (byte)0xFF};

	protected byte[] buf;
	protected int count;
	protected int pos;

	private int searchOffset;
	private int end;
	private InputStream is;
	private byte[] endMagic;

	private BinaryStream bs;
	private HeaderStream hs;


	public BinaryWithHeaderInputStream() {
		throw new Error("This is not the constructor you are looking for");
	}


	public BinaryWithHeaderInputStream(InputStream is) {
		this(is, END_MAGIC);
	}


	public BinaryWithHeaderInputStream(InputStream is, byte[] endMagic) {
		this.is = is;
		buf = new byte[32768];
		count = 0;
		pos = 0;

		searchOffset = 0;
		end = -1;
		this.endMagic = endMagic;

		bs = this.new BinaryStream();
		hs = this.new HeaderStream();
	}


	public HeaderStream getHeaderStream() {
		return hs;
	}


	public BinaryStream getBinaryStream() {
		return bs;
	}


	public class HeaderStream extends InputStream {
		private HeaderStream() {
		}


		public int available() throws IOException {
			int n = _readAvailable();
			return n;
		}

		public int read() throws IOException {
			byte[] b = new byte[1];

			int ret = read(b, 0, 1);
			if (ret < 1) {
				return -1;
			}

			return (int)(b[0] & 0xFF);
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
	}


	private class BinaryStream extends InputStream {
		private BinaryStream() {
		}


		public int available() throws IOException {
			if (end == -1) {
				throw new IOException("end magic not yet found");
			} else if (pos < end) {
				throw new IOException("HeaderStream not yet exhausted");
			}

			return count - (end + endMagic.length) + is.available();
		}

		public int read() throws IOException {
			byte[] b = new byte[1];

			int ret = read(b, 0, 1);
			if (ret < 1) {
				return -1;
			}

			return (int)(b[0] & 0xFF);
		}

		public int read(byte[] b, int off, int len) throws IOException {
			int n = 0;

			if (end == -1) {
				throw new IOException("end magic not yet found");
			} else if (pos < end) {
				throw new IOException("HeaderStream not yet exhausted");

			} else if (count > end + endMagic.length) {
				int nbInBuf = count - (end + endMagic.length);
				int nb = Math.min(nbInBuf, len);

				System.arraycopy(buf, end + endMagic.length, b, off, nb);
				off += nb;
				len -= nb;
				n = nb;
				count = end + endMagic.length;

				if (len > 0) {
					int ret = is.read(b, off, len);
					if (ret > 0) {
						n += ret;
					}
				}
				return n;
			}

			return is.read(b, off, len);
		}
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
		//int nbToRead = Math.min(buf.length - count,
		//		Math.max(is.available(), nbBytes));
		int nbToRead = Math.max(is.available(), nbBytes);

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
