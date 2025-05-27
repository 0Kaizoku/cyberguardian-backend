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
} 