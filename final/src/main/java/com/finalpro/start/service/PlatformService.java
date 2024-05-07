package com.finalpro.start.service;

import org.springframework.stereotype.Service;

@Service
public class PlatformService {
	public String detectPlatform() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            return "Windows";
        } else if (os.contains("mac")) {
            return "MacOS";
        } else if (os.contains("nix") || os.contains("nux") || os.contains("aix")) {
            return "Linux";
        } else {
            return "Unknown";
        }
    }
}
