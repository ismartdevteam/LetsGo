package mn.student.letsgo.whoisgonnapay;

import java.util.ArrayList;

import mn.student.letsgo.R;
import android.content.Context;
import android.content.res.Resources;
import android.text.Editable;
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

	private Context c;
    private ArrayList data;
    private static LayoutInflater inflater=null;
    public Resources res;
    UserListModel listModel=null;
    int i=0;
    
    public UserListAdapter(Context _c, ArrayList d,Resources resLocal) {
        c = _c;
        data=d;
        res = resLocal;
        inflater = (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		UserListModel uList = new UserListModel();
		//uList.setNickname("Player: " + );
		
    }
	
	@Override
	public int getCount() {
        return data.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	public static class ViewHolder{
    	
        public EditText nickName;
        public TextView text1;
        public Button btnEdit;
        public ImageView userColor;

    }

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View vi=convertView;
        ViewHolder holder = null;
        
        if(vi==null){ 
        	vi = inflater.inflate(R.layout.who_is_gonna_pay_single_user, null); 
            
        	holder=new ViewHolder();
            holder.nickName=(EditText)vi.findViewById(R.id.getUserNickName);
            holder.text1=(TextView)vi.findViewById(R.id.testId);
            holder.userColor=(ImageView)vi.findViewById(R.id.imgId);
            holder.btnEdit = (Button)vi.findViewById(R.id.btnSaveUser);
            
            vi.setTag(holder);
        }
        else {
            holder=(ViewHolder)vi.getTag();
        }

        listModel = (UserListModel) data.get(position);
        
	        /************  Set Model values in Holder elements ***********/
	    holder.nickName.setHint(listModel.getNickname());
	    holder.nickName.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if(count>0){
					
					listModel.setNickname(s.toString());
				}
				else 
					listModel.setNickname("Player: " + (position+1));
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {}
			
			@Override
			public void afterTextChanged(Editable s) {
				
			}
		});

	    
	    holder.userColor.setImageResource(res.getIdentifier("mn.student.letsgo:drawable/"+listModel.getId(),null,null));
	         
	    vi.setOnClickListener(new OnItemClickListener(position));

        return vi;
	}

	
	private class OnItemClickListener  implements OnClickListener{           
        private int mPosition;
        
        OnItemClickListener(int position){
        	 mPosition = position;
        }
        
        @Override
        public void onClick(View arg0) {
            Toast.makeText(c, String.valueOf(mPosition), Toast.LENGTH_SHORT).show();
        	//CustomListViewAndroidExample sct = (CustomListViewAndroidExample)activity;
        	//sct.onItemClick(mPosition);
        }               
    } 

	
}
