package demo.hxzk.com.customviewdemo.widget;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import demo.hxzk.com.customviewdemo.R;
import demo.hxzk.com.customviewdemo.util.PxUtils;


/**
 * 作者：Created by zjt on 2018/3/18
 * 文件描述：自定义RelativeLayout实现输入框
 */

public class CustomeEdittext extends RelativeLayout {
    //上下文
    Context context;

    //顶部文字
    String sTopMessage;
    //底部文字
    String sBottomText;
    //是否显示顶部文字
    boolean sIsShowTopMessage;


    //顶部文字TextView
    TextView tv_topMessage;
    //底部文字TextView
    TextView tv_bottomMessage;
    //输入框Edittext
    EditText edt_content;
    //底部下划线View
    View bottomLineView;



    private int duration = 200;//动画执行时间
    private int tvBottomPosition = 0;//tv_bottomMessage的当前位置，0：在输入框里；1：在tv_topMessage的位置（执行动画前判断）

    private int[] positionOne = new int[2];//tv_bottomMessage的默认位置坐标
    private int[] positionTwo = new int[2];//tv_topMessage的默认位置坐标
    private float[] fonts = new float[2];//tv_bottomMessage的默认大小


    //可用于在其他代码中直接new改控件
    public CustomeEdittext(Context context) {
        super(context);
        this.context = context;
        loadView(context);
    }

    //可用于在xml中直接使用该控件,注意AttributeSet参数获得在xml设置的属性
    public CustomeEdittext(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        getAttrs(context,attrs);
        loadView(context);
    }


    /**
     * 初始化布局view
     *
     * @param context
     */
    private void loadView(Context context) {
        View mView = LayoutInflater.from(context).inflate(R.layout.customview, this);
        initView(mView);
        initEvent();
    }



    private void initEvent() {
        edt_content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String conttext = editable.toString();
                //如果输入框没有内容,tv_bottomMessage从tv_topMessage的位置再移动回来
                if (conttext.length() == 0) {
                    //如果设置不显示顶部消息，那么不需要顶部TextView
                    if(!sIsShowTopMessage) {
                        tv_topMessage.setVisibility(VISIBLE);
                    }else {
                        tvBottomPosition = 0;
                        float startX = 0;
                        float endX = positionOne[0] - positionTwo[0];
                        float startY = 0;
                        float endY = positionOne[1] - positionTwo[1];
                        //执行动画
                        startAnim(startX, endX, startY, endY, fonts[1], fonts[0]);
                        tv_topMessage.setText(sBottomText);

                        bottomLineView.setBackgroundColor(Color.parseColor("#EBEBEB"));

                    }
                }
                //如果输入框开始输入字符，tv_bottomMessage使用动画移动到tv_topMessage的位置
                else if (conttext.length() >= 1 && tvBottomPosition == 0) {
                    if(!sIsShowTopMessage) {
                        tv_topMessage.setVisibility(INVISIBLE);
                    }else {
                        tvBottomPosition = 1;
                        float startX = positionOne[0] - positionTwo[0];
                        float endX = 0;
                        float startY = positionOne[1] - positionTwo[1];
                        float endY = 0;
                        //执行动画
                        startAnim(startX, endX, startY, endY, fonts[0], fonts[1]);
                        tv_topMessage.setText(sTopMessage);

                        bottomLineView.setBackgroundResource(R.color.colorAccent);

                        //输入内容的回调
                        String content =edt_content.getText().toString();
                        if(!TextUtils.isEmpty(content)){
                         onSuccessListener.onSuccess(content);
                        }
                    }
                }
            }
        });

    }

    /**
     * 获得输入的电话号
     * @return 输入的电话号
     */
    public String getContent(){
        return edt_content.getText().toString();
    }

    /**
     * 播放动画
     * @param startX 开始X
     * @param endX 目标X
     * @param startY 开始Y
     * @param endY 目标Y
     * @param startFont 开始字号
     * @param endFont 目标字号
     */
    private void startAnim(float startX, float endX, float startY, float endY, float startFont, float endFont){
        AnimatorSet set = new AnimatorSet();
        set.playTogether(
                ObjectAnimator.ofFloat(tv_topMessage , "TranslationX" , startX , endX),
                ObjectAnimator.ofFloat(tv_topMessage , "TranslationY" ,startY, endY),
                ObjectAnimator.ofFloat(tv_topMessage , "TextSize" , startFont, endFont));
        set.setDuration(duration).start();
    }

    /**
     * 自定义控件初始化完毕，可通过onWindowFocusChanged(),获取各控件的位置信息
     */
    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        //获得消息文本的位置信息
        tv_bottomMessage.getLocationInWindow(positionOne);
        tv_topMessage.getLocationOnScreen(positionTwo);

        //获得消息文本的字号信息
        fonts[0] =  PxUtils.px2sp(context,tv_bottomMessage.getTextSize());
        fonts[1] = PxUtils.px2sp(context,tv_topMessage.getTextSize());

        //初始化位置、字号（把tv_topMessage设置的与tv_bottomMessage显示一致）
        tv_topMessage.setTextSize(fonts[0]);
        tv_topMessage.setTranslationX(positionOne[0]-positionTwo[0]);
        tv_topMessage.setTranslationY(positionOne[1]-positionTwo[1]);
    }

    private void initView(View mView) {
        tv_topMessage = mView.findViewById(R.id.tv_topmessage);
        tv_bottomMessage = mView.findViewById(R.id.tv_bottomMessage);
        edt_content = mView.findViewById(R.id.edt_content);
        bottomLineView=mView.findViewById(R.id.lineview);

        //设置顶部文本信息,如果底部textview内容不为空则将提示信息设置到顶部textview
        if(tv_bottomMessage!=null && !TextUtils.isEmpty(sBottomText)){
            tv_topMessage.setText(sBottomText);
        }
    }



    /**
     * 获取自定义属性及对应的属性值
     *
     * @param context
     * @param attrs
     */
    private void getAttrs(Context context, AttributeSet attrs) {
        //获得 在attrs.xml custom.editext中已定义的属性集合
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.custome_edittext);
        sIsShowTopMessage = typedArray.getBoolean(R.styleable.custome_edittext_isShowTopMessage, false);
        sTopMessage = typedArray.getString(R.styleable.custome_edittext_topMessage);
        sBottomText = typedArray.getString(R.styleable.custome_edittext_bottomMessage);
        //释放资源
        typedArray.recycle();
    }


    /**
     * 输入完成回调
     */
    public interface OnSuccessListener{
        /**
         * 输入完成
         * @param phone 电话号
         */
        void onSuccess(String phone);
    }
    public OnSuccessListener onSuccessListener;

    /**
     * 设置监听
     * @param onSuccessListener
     */
    public void setOnSuccessListener(OnSuccessListener onSuccessListener){
        this.onSuccessListener = onSuccessListener;
    }

















}
