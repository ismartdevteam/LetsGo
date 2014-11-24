package mn.student.letsgo.whoisgonnapay;

import java.util.ArrayList;
import java.util.List;

import mn.student.letsgo.R;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class UserListAdapter extends BaseAdapter{

	ArrayList<UserListModel> data;
	UserListModel listModel;
	Context context; 
	LayoutInflater mInflater;
	
	public UserListAdapter(Context c, ArrayList<UserListModel> data){
		context = c;
		this.data = data;
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int arg0) {
		return arg0;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	public class ViewHolder {
	    EditText nickname;
	    ImageView userColor;
	    Button btnEditUser;
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup arg2) {
		final ViewHolder holder;
     	if (convertView == null) {
     		holder = new ViewHolder();
     		convertView = mInflater.inflate(R.layout.who_is_gonna_pay_single_user, null); // this is your cell
	        holder.userColor = (ImageView) convertView.findViewById(R.id.imgId);
	        holder.nickname = (EditText)convertView.findViewById(R.id.getUserNickName);
	        holder.nickname.setHint("Player: " + String.valueOf(position+1));
	        holder.btnEditUser = (Button)convertView.findViewById(R.id.btnDeleteUser);
	        holder.nickname.setTag(position); 
	        convertView.setTag(holder);	
	        listModel = new UserListModel();
     	}else {
			holder = (ViewHolder) convertView.getTag();
		}
     	
     	switch (position) {
      		case 0:
				((GradientDrawable)holder.userColor.getBackground())
		      		.setColor(context.getResources().getColor(R.color.user1));
				break;
			case 1:
				((GradientDrawable)holder.userColor.getBackground())
		      		.setColor(context.getResources().getColor(R.color.user2));
				break;
			case 2:
				((GradientDrawable)holder.userColor.getBackground())
		      		.setColor(context.getResources().getColor(R.color.user3));
				break;
			case 3:
				((GradientDrawable)holder.userColor.getBackground())
		      		.setColor(context.getResources().getColor(R.color.user4));
				break;
			case 4:
				((GradientDrawable)holder.userColor.getBackground())
		      		.setColor(context.getResources().getColor(R.color.user5));
				break;
			case 5:
				((GradientDrawable)holder.userColor.getBackground())
		      		.setColor(context.getResources().getColor(R.color.user6));
				break;

		default:
			break;
		}
     	String etLength = holder.nickname.getText().toString();
     	if(!TextUtils.isEmpty(etLength)){
     		data.get(position).setNickname(etLength);	
     	}
     	listModel.setNickname(etLength);
		 
		 holder.btnEditUser.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Toast.makeText(context, 
						holder.nickname.getText().toString(), Toast.LENGTH_SHORT).show();
			}
		});
    return convertView;
    }
}
