package com.example.projecttemplate;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentTwo#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentTwo extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private EditText Phone;
    private EditText msg;

    private Button btn_pick;
    private Button btn_send;


    //联系人申请权限
    private final ActivityResultLauncher permissionLauncher2 = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            new ActivityResultCallback<Boolean>() {
                @Override
                public void onActivityResult(Boolean result) {

                    //true表示申请成功
                    if (result) {
                        contactLauncher.launch(null);
                    }
                }
            }
    );

    //短信权限申请
    private final ActivityResultLauncher permissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            new ActivityResultCallback<Boolean>() {
                @Override
                public void onActivityResult(Boolean result) {

                    //true表示申请成功
                    if (result) {
                        Log.d("flag", "onActivityResult: 申请成功");
                    }
                }
            }
    );


    //    启动查看联系人窗口
    ActivityResultLauncher contactLauncher = registerForActivityResult(
            new ActivityResultContracts.PickContact(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri uri) {
                    Log.d("flag", uri.toString());
                    Map<String, List<String>> map = getPhones(uri);

                    //遍历Map
                    Set entrySet = map.entrySet();
                    Iterator it = entrySet.iterator();
                    while (it.hasNext()) {
                        Map.Entry me = (Map.Entry) it.next();
                        String name = (String) me.getKey();
                        List<String> phone = (List<String>) me.getValue();

                        //                        Log.d("flag", "联系人=" + name);
                        //                        Log.d("flag", "电话=" + Arrays.toString(phone.toArray()));
                        Phone.setText(phone.get(0));

                    }
                }
            });


    //联系人权限申请
    private void requestPermission2() {
        permissionLauncher2.launch(Manifest.permission.READ_CONTACTS);
    }
    //短信权限申请
    private void requestPermission() {
        permissionLauncher.launch(Manifest.permission.SEND_SMS);
    }

    public Map<String, List<String>> getPhones(Uri uri) {
        Map<String, List<String>> map = new HashMap<>(); //String存放联系人名，List<String>存放手机号
        List<String> phones = new ArrayList<>();
        Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {

            //获取联系人名称
            int name_index = cursor.getColumnIndex("display_name"); //联系人字段
            String name = cursor.getString(name_index);

            //取得联系人的ID索引值
            int id_index = cursor.getColumnIndex("_id"); //id字段
            String contactId = cursor.getString(id_index);

            // 注：电话数据集在 ContactsContract.CommonDataKinds.Phone.CONTENT_URI

            // 使用条件查询：以上面的 contactId 值为条件来查询对应联系人的电话
            Cursor cursor2 = getActivity().getContentResolver().query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null, "contact_id = ?", new String[]{contactId}, null);

            //一个人可能有几个号码
            if (cursor2 != null && cursor2.moveToFirst()) {
                do {
                    int index = cursor2.getColumnIndex("data1"); //"data1"是电话号码字段
                    String phone = cursor2.getString(index);
                    phones.add(phone); //电话放到List集合
                } while (cursor2.moveToNext());
                cursor2.close();
                map.put(name, phones); //联系人为key，电话为value，放到Map集合
            }
            cursor.close();
        }
        return map;
    }


    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_pick:
                    getContact();
                    break;
                case R.id.btn_sendsms:
                    String s1 = Phone.getText().toString();   //联系人电话号码
                    String s2 = msg.getText().toString();    //短信内容
                    sendSMS(s1, s2);
                    break;
            }
        }
    };


    public FragmentTwo() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentOne.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentTwo newInstance(String param1, String param2) {
        FragmentTwo fragment = new FragmentTwo();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_two, container, false);

        btn_pick = view.findViewById(R.id.btn_pick);      //选取联系人
        btn_send = view.findViewById(R.id.btn_sendsms);   //发送按钮

        Phone = view.findViewById(R.id.et_phone);       //联系人文本框
        msg = view.findViewById(R.id.et_msg);           //短信内容文本框


        btn_pick.setOnClickListener(listener);
        btn_send.setOnClickListener(listener);

        return view;
    }


    private void getContact() {
        //选取联系人
        requestPermission2();
        Log.d("flag", "选取联系人");

    }

    private void sendSMS(String s1, String s2) {

        requestPermission();
//        Intent intent = new Intent( getActivity(), TwoService.class );
//        intent.putExtra("phone",s1);
//        intent.putExtra("sms", s2);
//        getActivity().startService(intent);



        //使用闹钟+SmsManager定时发送短信
        // 获得系统日期，用于设置TimePickerDialog的初始值
        Calendar calender = Calendar.getInstance();

        // 时间选择对话框
        TimePickerDialog tpd = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                // ① 创建Intent
                Intent intent = new Intent( getActivity(), TwoService.class );
                intent.putExtra("phone",s1);
                intent.putExtra("sms",s2);

                // ② 创建PendingIntent
                PendingIntent pendingIntent = PendingIntent.getService( getActivity(), 100, intent, PendingIntent.FLAG_IMMUTABLE);
                // 创建临时Calendar对象，目的：将设置的时:分转为毫秒数给闹钟设置定时时间


                // 临时Calendar对象的值来自TimePickerDialog
                Calendar tmp = Calendar.getInstance();
                tmp.set(Calendar.HOUR_OF_DAY, hourOfDay); //设置时
                tmp.set(Calendar.MINUTE, minute); //设置分
                tmp.set(Calendar.SECOND, 0); //设置秒

                // ③ 创建闹钟（一次性任务）
                AlarmManager manager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
                manager.set( AlarmManager.RTC_WAKEUP, tmp.getTimeInMillis(), pendingIntent );
                Toast.makeText(getActivity(), "闹钟设置成功", Toast.LENGTH_SHORT).show();
            } //end onTimeSet
        }, calender.get(Calendar.HOUR_OF_DAY), calender.get(Calendar.MINUTE), true ); //end TimePickerDialog
        tpd.show(); //显示日期选择对话框

        Log.d("flag", s1 + s2);
    }
}