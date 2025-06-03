package com.cyberguardian.service;

import java.util.*;

public class PermissionChecker {
    private static final Set<String> dangerousPermissions = new HashSet<>(Arrays.asList(
        "android.permission.READ_SMS",
        "android.permission.SEND_SMS",
        "android.permission.RECEIVE_SMS",
        "android.permission.CALL_PHONE",
        "android.permission.READ_CONTACTS",
        "android.permission.WRITE_CONTACTS",
        "android.permission.RECORD_AUDIO",
        "android.permission.CAMERA",
        "android.permission.ACCESS_FINE_LOCATION",
        "android.permission.ACCESS_COARSE_LOCATION",
        "android.permission.READ_PHONE_STATE",
        "android.permission.READ_CALL_LOG",
        "android.permission.WRITE_CALL_LOG",
        "android.permission.READ_EXTERNAL_STORAGE",
        "android.permission.WRITE_EXTERNAL_STORAGE",
        "android.permission.BLUETOOTH_CONNECT"
    ));

    private static final Map<String, String> permissionMapping = new HashMap<>();
    static {
        permissionMapping.put("android.permission.ACCESS_FINE_LOCATION", "Your location : fine (GPS) location (D)");
        permissionMapping.put("android.permission.ACCESS_COARSE_LOCATION", "Your location : coarse (network-based) location (D)");
        permissionMapping.put("android.permission.CAMERA", "Hardware controls : take pictures and videos (D)");
        permissionMapping.put("android.permission.RECORD_AUDIO", "Hardware controls : record audio (D)");
        permissionMapping.put("android.permission.READ_CONTACTS", "Your personal information : read contact data (D)");
        permissionMapping.put("android.permission.WRITE_CONTACTS", "Your personal information : write contact data (D)");
        permissionMapping.put("android.permission.INTERNET", "Network communication : full Internet access (D)");
        permissionMapping.put("android.permission.SEND_SMS", "Services that cost you money : send SMS messages (D)");
        permissionMapping.put("android.permission.RECEIVE_SMS", "Your messages : receive SMS (D)");
        permissionMapping.put("android.permission.READ_SMS", "Your messages : read SMS or MMS (D)");
        permissionMapping.put("android.permission.READ_EXTERNAL_STORAGE", "Storage : modify/delete USB storage contents modify/delete SD card contents (D)");
        permissionMapping.put("android.permission.WRITE_EXTERNAL_STORAGE", "Storage : modify/delete USB storage contents modify/delete SD card contents (D)");
        permissionMapping.put("android.permission.BLUETOOTH_CONNECT", "Hardware controls : control Bluetooth (D)");
        permissionMapping.put("android.permission.ACCESS_WIFI_STATE", "Network communication : view Wi-Fi state (S)");
        permissionMapping.put("android.permission.CHANGE_WIFI_STATE", "System tools : change Wi-Fi state (D)");
        permissionMapping.put("android.permission.READ_PHONE_STATE", "Phone calls : read phone state and identity (D)");
        permissionMapping.put("android.permission.CALL_PHONE", "Services that cost you money : directly call phone numbers (D)");
        permissionMapping.put("android.permission.READ_CALL_LOG", "Phone calls : read phone state and identity (D)");
        permissionMapping.put("android.permission.WRITE_CALL_LOG", "Phone calls : modify phone state (S)");
        permissionMapping.put("android.permission.ACCESS_NETWORK_STATE", "Network communication : view network state (S)");
        permissionMapping.put("android.permission.RECEIVE_MMS", "Your messages : receive MMS (D)");
        permissionMapping.put("android.permission.RECEIVE_WAP_PUSH", "Your messages : receive WAP (D)");
        permissionMapping.put("android.permission.SEND_RESPOND_VIA_MESSAGE", "Your messages : send SMS-received broadcast (S)");
        permissionMapping.put("android.permission.USE_SIP", "Network communication : make/receive Internet calls (D)");
        permissionMapping.put("android.permission.GET_ACCOUNTS", "Your accounts : discover known accounts (S)");
        permissionMapping.put("android.permission.MANAGE_ACCOUNTS", "Your accounts : manage the accounts list (D)");
        permissionMapping.put("android.permission.AUTHENTICATE_ACCOUNTS", "Your accounts : act as an account authenticator (D)");
        permissionMapping.put("android.permission.WRITE_SYNC_SETTINGS", "System tools : write sync settings (D)");
        permissionMapping.put("android.permission.READ_SYNC_SETTINGS", "System tools : read sync settings (S)");
        permissionMapping.put("android.permission.READ_SYNC_STATS", "System tools : read sync statistics (S)");
        permissionMapping.put("android.permission.SET_WALLPAPER", "System tools : set wallpaper (S)");
        permissionMapping.put("android.permission.SET_WALLPAPER_HINTS", "System tools : set wallpaper size hints (S)");
        permissionMapping.put("android.permission.BLUETOOTH_ADMIN", "System tools : bluetooth administration (D)");
        permissionMapping.put("android.permission.BLUETOOTH", "Hardware controls : connect to Bluetooth devices (D)");
        permissionMapping.put("android.permission.MODIFY_AUDIO_SETTINGS", "Hardware controls : change your audio settings (D)");
        permissionMapping.put("android.permission.NFC", "Network communication : control Near Field Communication (D)");
        permissionMapping.put("android.permission.WAKE_LOCK", "System tools : prevent device from sleeping (D)");
        permissionMapping.put("android.permission.VIBRATE", "Hardware controls : control vibrator (S)");
        permissionMapping.put("android.permission.FLASHLIGHT", "Hardware controls : control flashlight (S)");
        permissionMapping.put("android.permission.BODY_SENSORS", "Hardware controls : test hardware (S)");
        permissionMapping.put("android.permission.REQUEST_INSTALL_PACKAGES", "Default : directly install applications (S)");
        permissionMapping.put("android.permission.SYSTEM_ALERT_WINDOW", "System tools : display system-level alerts (D)");
        permissionMapping.put("android.permission.WRITE_SETTINGS", "System tools : modify global system settings (D)");
        permissionMapping.put("android.permission.READ_CALENDAR", "Your personal information : read calendar events (D)");
        permissionMapping.put("android.permission.WRITE_CALENDAR", "Your personal information : add or modify calendar events and send email to guests (D)");
        permissionMapping.put("android.permission.READ_USER_DICTIONARY", "Your personal information : read user defined dictionary (D)");
        permissionMapping.put("android.permission.WRITE_USER_DICTIONARY", "Your personal information : write to user defined dictionary (S)");
        permissionMapping.put("android.permission.SET_ALARM", "Your personal information : set alarm in alarm clock (S)");
        // ... Add more as needed from the user's list
    }

    public static boolean isDangerousPermission(String permission) {
        return dangerousPermissions.contains(permission);
    }

    public static double calculatePermissionBasedRiskScore(List<String> permissions) {
        if (permissions == null || permissions.isEmpty()) return 0.0;
        long dangerousCount = permissions.stream().filter(PermissionChecker::isDangerousPermission).count();
        double baseScore = (double) dangerousCount / permissions.size();

        Map<String, List<String>> permissionCategories = new HashMap<>();
        for (String permission : permissions) {
            String category = "OTHER";
            if (permission.contains("INTERNET")) category = "INTERNET";
            else if (permission.contains("SMS")) category = "SMS";
            else if (permission.contains("LOCATION")) category = "LOCATION";
            else if (permission.contains("CAMERA")) category = "CAMERA";
            else if (permission.contains("CONTACTS")) category = "CONTACTS";
            permissionCategories.computeIfAbsent(category, k -> new ArrayList<>()).add(permission);
        }
        double combinationBonus = 0.0;
        if (permissionCategories.containsKey("INTERNET")) {
            if (permissionCategories.containsKey("SMS")) combinationBonus += 0.2;
            if (permissionCategories.containsKey("LOCATION")) combinationBonus += 0.1;
            if (permissionCategories.containsKey("CAMERA")) combinationBonus += 0.15;
            if (permissionCategories.containsKey("CONTACTS")) combinationBonus += 0.15;
        }
        return Math.min(baseScore + combinationBonus, 1.0);
    }

    public static String determineRiskLevel(double riskScore) {
        if (riskScore < 0.3) return "BENIGN";
        else if (riskScore < 0.6) return "SUSPICIOUS";
        else if (riskScore <= 1.0) return "MALICIOUS";
        else return "UNKNOWN";
    }

    public static List<String> mapPermissions(List<String> androidPermissions) {
        List<String> mapped = new ArrayList<>();
        for (String perm : androidPermissions) {
            String mappedPerm = permissionMapping.get(perm);
            if (mappedPerm != null) {
                mapped.add(mappedPerm);
            }
        }
        return mapped;
    }
} 