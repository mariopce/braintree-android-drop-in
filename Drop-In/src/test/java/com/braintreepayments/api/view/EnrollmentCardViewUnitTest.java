package com.braintreepayments.api.view;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.braintreepayments.api.dropin.R;
import com.braintreepayments.api.dropin.interfaces.AddPaymentUpdateListener;
import com.braintreepayments.api.dropin.view.EnrollmentCardView;
import com.braintreepayments.api.test.UnitTestActivity;
import com.braintreepayments.cardform.view.ErrorEditText;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.util.ActivityController;

import static com.braintreepayments.api.test.ColorTestUtils.setupActivity;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.assertj.android.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricGradleTestRunner.class)
public class EnrollmentCardViewUnitTest {

    private ActivityController mActivityController;
    private Activity mActivity;
    private EnrollmentCardView mView;

    @Before
    public void setup() {
        UnitTestActivity.view = R.layout.bt_add_card_activity;
        mActivityController = Robolectric.buildActivity(UnitTestActivity.class);
        mActivity = (Activity) mActivityController.setup().get();
        mView = (EnrollmentCardView) mActivity.findViewById(R.id.bt_enrollment_card_view);
    }

    @Test
    public void setup_correctIconBasedOnBackground() {
        Activity darkActivity = setupActivity(R.color.bt_black);
        mView.setup(darkActivity);
        assertDrawableIsFromResource(R.id.bt_sms_code_icon, R.drawable.bt_ic_sms_code_dark);

        Activity lightActivity = setupActivity(R.color.bt_white);
        mView.setup(lightActivity);
        assertDrawableIsFromResource(R.id.bt_sms_code_icon, R.drawable.bt_ic_sms_code);
    }

    @Test
    public void phoneNumberIsDisplayedCorrectly() {
        mView.setPhoneNumber("1231231234");

        assertThat((TextView) mView.findViewById(R.id.bt_sms_sent_text)).hasText("Enter the SMS code sent to 1231231234");
    }

    @Test
    public void getSmsCode_returnsSmsCode() {
        ((TextView) mView.findViewById(R.id.bt_sms_code)).setText("1234");

        assertEquals("1234", mView.getSmsCode());
    }

    @Test
    public void setsImeOptionsonSmsEditText() {
        EditText smsCode = (EditText) mView.findViewById(R.id.bt_sms_code);

        assertEquals(EditorInfo.IME_ACTION_GO, smsCode.getImeOptions());
        assertEquals(RuntimeEnvironment.application.getString(R.string.bt_confirm), smsCode.getImeActionLabel());
    }

    @Test
    public void onEditorAction_submitsForm() {
        ((EditText) mView.findViewById(R.id.bt_sms_code)).setText("123456");
        mView.onEditorAction(null, 0, null);

        assertThat(mView.findViewById(R.id.bt_button)).isGone();
        assertThat(mView.findViewById(R.id.bt_animated_button_loading_indicator)).isVisible();
    }

    @Test
    public void onClick_doesNothingIfListenerNotSet() {
        mView.setAddPaymentUpdatedListener(null);

        mView.onClick(mView.findViewById(R.id.bt_animated_button_view));
    }

    @Test
    public void onClick_helpButtonGoesBack() {
        AddPaymentUpdateListener listener = mock(AddPaymentUpdateListener.class);
        mView.setAddPaymentUpdatedListener(listener);

        mView.onClick(mView.findViewById(R.id.bt_sms_help_button));

        verify(listener).onBackRequested(mView);
    }

    @Test
    public void onClick_confirmButtonCallsPaymentUpdated() {
        AddPaymentUpdateListener listener = mock(AddPaymentUpdateListener.class);
        mView.setAddPaymentUpdatedListener(listener);
        ((EditText) mView.findViewById(R.id.bt_sms_code)).setText("123456");

        mView.onClick(mView.findViewById(R.id.bt_animated_button_view));

        verify(listener).onPaymentUpdated(mView);
    }

    @Test
    public void onClick_showsErrorWhenEditTextIsEmpty() {
        mView.onClick(mView.findViewById(R.id.bt_animated_button_view));

        assertThat(mView.findViewById(R.id.bt_button)).isVisible();
        assertThat(mView.findViewById(R.id.bt_animated_button_loading_indicator)).isGone();
        assertTrue(((ErrorEditText) mView.findViewById(R.id.bt_sms_code)).isError());
    }

    private void assertDrawableIsFromResource(int view, int resourceId) {
        Drawable drawable = ((ImageView) mView.findViewById(view)).getDrawable();
        assertEquals(resourceId, shadowOf(drawable).getCreatedFromResId());
    }
}
