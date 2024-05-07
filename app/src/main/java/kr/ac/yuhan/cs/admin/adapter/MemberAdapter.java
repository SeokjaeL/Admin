package kr.ac.yuhan.cs.admin.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import kr.ac.yuhan.cs.admin.data.AdminData;
import kr.ac.yuhan.cs.admin.data.MemberData;
import kr.ac.yuhan.cs.admin.R;


public class MemberAdapter extends BaseAdapter {
    private ArrayList<MemberData> memberList;
    private LayoutInflater inflater;
    private Context context; // Context 추가

    public MemberAdapter(Context context, ArrayList<MemberData> memberList) {
        this.context = context;
        this.memberList = memberList;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return memberList.size();
    }

    @Override
    public Object getItem(int position) {
        return memberList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            // 뷰홀더 초기화
            convertView = inflater.inflate(R.layout.member_list, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.numberTextView = convertView.findViewById(R.id.number);
            viewHolder.userIdTextView = convertView.findViewById(R.id.userId);
            viewHolder.userPointTextView = convertView.findViewById(R.id.userPoint);
            viewHolder.outBtn = convertView.findViewById(R.id.outBtn); // outBtn 초기화
            convertView.setTag(viewHolder);
        } else {
            // 뷰홀더 재사용
            viewHolder = (ViewHolder) convertView.getTag();
        }

        MemberData memberData = memberList.get(position);
        viewHolder.numberTextView.setText(String.valueOf(memberData.getNumber()));
        viewHolder.userIdTextView.setText(memberData.getUserId());
        viewHolder.userPointTextView.setText(String.valueOf(memberData.getPoint()));

        // 삭제 버튼 클릭 이벤트 리스너 설정
        viewHolder.outBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // AlertDialog 생성
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("해당 데이터를 삭제하시겠습니까?");
                builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // "예"를 선택했을 때의 동작
                        String uid = memberData.getUid();// 사용자의 UID
                        FirebaseFirestore db = FirebaseFirestore.getInstance();

                        // Firestore에서 사용자 데이터 삭제
                        db.collection("User").document(uid)
                                .delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // 성공적으로 Firestore에서 사용자 데이터가 삭제된 경우,
                                        // Firebase Cloud Functions를 호출하여 Firebase Authentication에서도 사용자를 삭제
                                        deleteFirebaseUser(uid);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(context, "데이터 삭제 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                        removeMember(position); // 데이터 삭제
                    }
                });
                builder.setNegativeButton("아니오", null); // "아니오"를 선택했을 때의 동작은 아무것도 하지 않음
                builder.show(); // AlertDialog 표시
            }
        });

        return convertView;
    }

    // 데이터 삭제 메서드
    public void removeMember(int position) {
        memberList.remove(position);
        // 삭제된 아이템 이후의 모든 아이템들의 번호를 갱신
        for (int i = position; i < memberList.size(); i++) {
            MemberData item = memberList.get(i);
            item.setNumber(i + 1);  // 번호를 현재 인덱스에 맞게 재설정
        }
        notifyDataSetChanged(); // 변경된 데이터셋을 알려 ListView를 갱신
    }

    public void updateData(ArrayList<MemberData> data) {
        this.memberList = data;
        notifyDataSetChanged();
    }

    // Firebase Cloud Functions를 호출하여 Firebase Authentication에서 사용자 삭제
    private void deleteFirebaseUser(String uid) {
        Map<String, Object> data = new HashMap<>();
        data.put("uid", uid);

        // Firebase Cloud Functions를 호출하여 Firebase Authentication에서 사용자를 삭제
        FirebaseFunctions.getInstance()
                .getHttpsCallable("deleteUser")
                .call(data)
                .addOnSuccessListener(new OnSuccessListener<HttpsCallableResult>() {
                    @Override
                    public void onSuccess(HttpsCallableResult httpsCallableResult) {
                        // Firebase Cloud Functions 호출이 성공한 경우
                        Map<String, Object> result = (Map<String, Object>) httpsCallableResult.getData();
                        if (result.containsKey("success")) {
                            // 사용자 삭제가 성공한 경우
                            // 추가적인 동작이 필요한 경우 여기에 작성
                            Toast.makeText(context, "사용자 데이터가 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                        } else if (result.containsKey("error")) {
                            // 사용자 삭제 중에 오류가 발생한 경우
                            String errorMessage = (String) result.get("error");
                            Toast.makeText(context, "사용자 삭제 실패: " + errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Firebase Cloud Functions 호출 중에 오류가 발생한 경우
                        Toast.makeText(context, "사용자 삭제 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    static class ViewHolder {
        TextView numberTextView;
        TextView userIdTextView;
        TextView userPointTextView;
        ImageView outBtn; // outBtn 추가
    }
}