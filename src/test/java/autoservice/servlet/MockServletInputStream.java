package autoservice.servlet;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;

import java.io.ByteArrayInputStream;

public class MockServletInputStream extends ServletInputStream {
    private final ByteArrayInputStream byteArrayInputStream;

    public MockServletInputStream(ByteArrayInputStream byteArrayInputStream) {
        this.byteArrayInputStream = byteArrayInputStream;
    }

    @Override
    public int read() {
        return byteArrayInputStream.read();
    }

    @Override
    public boolean isFinished() {
        return byteArrayInputStream.available() == 0;
    }

    @Override
    public boolean isReady() {
        return true;
    }

    @Override
    public void setReadListener(ReadListener readListener) {
    }
}