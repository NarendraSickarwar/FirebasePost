package com.home.ui.utils;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Manish Kumar
 *         <p>
 *         PermissionsHelper
 *         <p>
 *         <p>
 *         This class is use for  handle run time permission in application
 *         <p>
 */
public class PermissionsHelper {
    /**
     * Request code for permissions
     */
    public static final int PermissionrequestCode = 1223;

    /**
     * All Permission which is use in Application
     */
    private static final List<String> permissions = Arrays.asList(
            Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE
    );

    /**
     * use for check permission is already requesting or not
     */
    public static boolean requestrunning = false;


    /**
     * use for get all permission list which is set in  {@link #permissions}
     *
     * @param context
     * @return
     */
    private static List<String> getPermissionConstants(Context context) {
        return permissions;
    }


    /**
     * use for get {@link PermissionInfo} of all @param required
     *
     * @param context
     * @param required
     * @return
     */
    private static List<PermissionInfo> getPermissions(Context context, List<String> required) {

        List<PermissionInfo> permissionInfoList = new ArrayList<>();

        PackageManager pm = context.getPackageManager();
        for (String permission : required) {
            PermissionInfo pinfo = null;
            try {
                pinfo = pm.getPermissionInfo(permission, PackageManager.GET_META_DATA);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                continue;
            }

            permissionInfoList.add(pinfo);
        }
        return permissionInfoList;
    }

    /**
     * use for get name of permission for all @param required
     *
     * @param context
     * @param required
     * @return
     */
    private static String[] getPermissionNames(Context context, List<String> required) {
        PackageManager pm = context.getPackageManager();
        String[] names = new String[required.size()];
        int i = 0;
        for (PermissionInfo permissionInfo : getPermissions(context, required)) {
            CharSequence label = permissionInfo.loadLabel(pm);
            names[i] = label.toString();
            i++;
        }
        return names;
    }


    /**
     * use for show Alert Dialog with @param required
     *
     * @param context
     * @param title
     * @param required
     */
    public static void show(final Context context, String title, final List<String> required) {
        if (required == null) return;
        if (requestrunning) return;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (title != null) {
            builder.setTitle(title);
        }

        ArrayAdapter adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, getPermissionNames(context, required));
        builder.setAdapter(adapter, null);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                requestrunning = true;
                ActivityCompat.requestPermissions(scanForActivity(context),
                        required.toArray(new String[required.size()]), PermissionrequestCode);
            }
        });
        builder.setCancelable(false);


        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * use for get {@link Activity} from {@link Context}
     *
     * @param cont
     * @return
     */
    private static Activity scanForActivity(Context cont) {
        if (cont == null)
            return null;
        else if (cont instanceof Activity)
            return (Activity) cont;
        else if (cont instanceof ContextWrapper)
            return scanForActivity(((ContextWrapper) cont).getBaseContext());

        return null;
    }

    /**
     * use for check @param permission is PERMISSION_GRANTED or not
     *
     * @param context
     * @param permission
     * @return
     */
    private static boolean isPermissionGranted(Context context, String permission) {
        return PermissionChecker.checkSelfPermission(context, permission) ==
                PackageManager.PERMISSION_GRANTED;
    }

    /**
     * use for check @param permission is PERMISSION_DENIED or not
     *
     * @param context
     * @param permission
     * @return
     */
    private static boolean isPermissionDenied(Context context, String permission) {
        return PermissionChecker.checkSelfPermission(context, permission) ==
                PackageManager.PERMISSION_DENIED;
    }

    /**
     * use for check RunTime Permission is required or not
     *
     * @return
     */
    public static boolean areExplicitPermissionsRequired() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    /**
     * use for show lert Dialog {@link #show(Context, String, List)}
     *
     * @param context
     */
    public static void show(final Context context) {
        show(context, null, null);
    }

    /**
     * use for check all permission PERMISSION_GRANTED or not
     * if all permissions are granted then blank list is returned other wise not granted
     * permissions list is return
     *
     * @param context
     * @return
     */
    public static List<String> isAllPremissiongranted(Context context) {
        List<String> premissions = getPermissionConstants(context);
        List<String> requiredPremisiion = new ArrayList<String>();
        for (int i = 0; i < premissions.size(); i++) {
            if (!isPermissionGranted(context, premissions.get(i))) {
                requiredPremisiion.add(premissions.get(i));
            }

        }
        return requiredPremisiion;
    }

    /**
     * use for check permission is deny with never ask again
     *
     * @param activity
     * @param permission
     * @return
     */
    private static boolean isPermissionDenyWithNeverAsk(Activity activity, String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (isPermissionDenied(activity, permission)) {
                boolean reational = activity.shouldShowRequestPermissionRationale(permission);
                //Utils.printLog(permission + ", reatianal=" + reational);
                return reational;
            }
        }
        return false;
    }

    /**
     * use for get permission which is deny with never ask again
     *
     * @param context
     * @return
     */
    public static List<String> checkDenyWithNeverAskAgain(Activity context) {
        List<String> premissions = getPermissionConstants(context);
        List<String> requiredPremisiion = new ArrayList<String>();
        for (int i = 0; i < premissions.size(); i++) {
            if (isPermissionDenyWithNeverAsk(context, premissions.get(i))) {
                requiredPremisiion.add(premissions.get(i));
            }

        }
        return requiredPremisiion;
    }

}
