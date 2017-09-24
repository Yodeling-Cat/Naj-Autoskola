// Copyright (c) 2015-2017. Spiracle Software. All Rights Reserved.

package com.spiraclestudios.autoskola.presentation.ui.fragments;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import com.spiraclestudios.autoskola.R;
import com.spiraclestudios.autoskola.TestFragmentInteractor;
import com.spiraclestudios.autoskola.TestPagerAdapter;
import com.spiraclestudios.autoskola.presentation.ui.activities.TestActivity.TestTypes;
import java.util.ArrayList;
import java.util.List;

import static android.content.ClipData.newPlainText;
import static android.content.Context.CLIPBOARD_SERVICE;
import static android.graphics.PorterDuff.Mode.MULTIPLY;
import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.LOLLIPOP;
import static android.widget.Toast.LENGTH_SHORT;
import static com.spiraclestudios.autoskola.framework.platform.AttributeResolver.resolveColorAttr;

public class TestFragment extends Fragment {

  private static final float QUESTION_IMAGE_SCALE_MULTIPLIER = 1.3f;

  @BindView(R.id.scroll_view) ScrollView scroll_view;
  @BindView(R.id.top_space) Space top_space;
  @BindView(R.id.question_text) TextView question_text;
  @BindView(R.id.question_image) ImageButton question_image;
  @BindView(R.id.intersection_image) ImageButton intersection_image;
  //@BindView(R.id.intersection_canvas) IntersectionCanvas intersection_canvas;
  @BindView(R.id.answer1) AppCompatButton answer_button_1;
  @BindView(R.id.answer2) AppCompatButton answer_button_2;
  @BindView(R.id.answer3) AppCompatButton answer_button_3;
  @BindView(R.id.answer1_chosen_status) TextView answer1_chosen_status;
  @BindView(R.id.answer2_chosen_status) TextView answer2_chosen_status;
  @BindView(R.id.answer3_chosen_status) TextView answer3_chosen_status;

  public TestFragmentInteractor interactor;
  public TestPagerAdapter testAdapter;
  public int questionIdx;
  private boolean isQuestionImageExpanded;

  public static TestFragment newInstance(int position) {
    Bundle bundle = new Bundle();
    bundle.putInt("position", position);

    TestFragment fragment = new TestFragment();
    fragment.setArguments(bundle);
    return fragment;
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.test__content_fragment, container, false);
    ButterKnife.bind(this, view);

    initiateFragment();

    return view;
  }

  /**
   * Should be fully visually setup and have all data loaded
   */
  private void initiateFragment() {
    interactor = (TestFragmentInteractor) getActivity();
    testAdapter = ((TestFragmentInteractor) getActivity()).getTestPagerAdapter();

    Bundle args = getArguments();
    questionIdx = args.getInt("position");

    setQuestionText(interactor.getQuestionText(questionIdx));
    setQuestionImage(interactor.getQuestionImage(questionIdx));
    setTextsForAnswers();
    handleChosenAnswerStatusTexts(interactor.getChosenAnswer(questionIdx));

    highlightAnswer(interactor.getChosenAnswer(questionIdx));
  }

  private void setTextsForAnswers() {
    List<String> answers = interactor.getQuestionAnswers(questionIdx);
    answer_button_1.setText(answers.get(0));
    answer_button_2.setText(answers.get(1));
    answer_button_3.setText(answers.get(2));
  }

  public void handleChosenAnswerStatusTexts(int chosenAnswer) {
    List<TextView> statusTexts = new ArrayList<>();
    statusTexts.add(answer1_chosen_status);
    statusTexts.add(answer2_chosen_status);
    statusTexts.add(answer3_chosen_status);

    if (!interactor.getCompleted() || interactor.getTestType() == TestTypes.CORRECT_ANSWERS) {
      for (TextView statusText : statusTexts) {
        statusText.setVisibility(View.GONE);
      }
      return;
    }

    for (int i = 0; i < statusTexts.size(); i++) {
      TextView statusText = statusTexts.get(i);
      if (i == interactor.getCorrectAnswer(questionIdx) - 1) {
        if (chosenAnswer == 0) {
          statusText.setText(getResources().getString(R.string.test__text__no_answer_chosen));
        } else if (i == chosenAnswer - 1) {
          statusText.setText(getResources().getString(R.string.test__text__chosen_answer));
        } else {
          statusText.setText(getResources().getString(R.string.test__text__correct_answer));
        }
        statusText.setVisibility(View.VISIBLE);
      } else if (chosenAnswer != 0 && i == chosenAnswer - 1) {
        statusText.setText(getResources().getString(R.string.test__text__chosen_answer));
        statusText.setVisibility(View.VISIBLE);
      } else {
        statusText.setVisibility(View.GONE);
      }
    }
  }

  @Override public void onStart() {
    super.onStart();
    testAdapter.addTestPagerListener(this);
  }

  @Override public void onStop() {
    super.onStop();
    testAdapter.removeTestPagerListener(this);
  }

  public void onTestCompleted() {
    highlightAnswer(interactor.getChosenAnswer(questionIdx));
    handleChosenAnswerStatusTexts(interactor.getChosenAnswer(questionIdx));
  }

  @OnClick(R.id.question_image) public void question_image_onClick() {
    if (interactor.getQuestionType(questionIdx) != 1) return;

    isQuestionImageExpanded = !isQuestionImageExpanded;

    if (isQuestionImageExpanded) {
      question_image.setMaxHeight(
          (int) (question_image.getMaxHeight() * QUESTION_IMAGE_SCALE_MULTIPLIER));
    } else {
      question_image.setMaxHeight(
          (int) (question_image.getMaxHeight() / QUESTION_IMAGE_SCALE_MULTIPLIER));
    }
    question_image.requestLayout();
  }

  @OnLongClick(R.id.question_text) public boolean copyQuestionToClipboard() {
    String label =
        String.format(getString(R.string.test__clipboard_label__question_text), questionIdx + 1);
    ClipData clip = newPlainText(label, question_text.getText().toString());

    ((ClipboardManager) getActivity().getSystemService(CLIPBOARD_SERVICE)).setPrimaryClip(clip);

    Toast.makeText(getActivity(), R.string.test__toast__question_was_copied, LENGTH_SHORT).show();
    return true;
  }

  @OnLongClick({ R.id.answer1, R.id.answer2, R.id.answer3 })
  public boolean copyAnswerToClipboard(Button button) {
    String label = getString(R.string.test__clipboard_label__answer_text);
    ClipData clip = newPlainText(label, button.getText().toString());

    ((ClipboardManager) getActivity().getSystemService(CLIPBOARD_SERVICE)).setPrimaryClip(clip);

    Toast.makeText(getActivity(), R.string.test__toast__answer_was_copied, LENGTH_SHORT).show();
    return true;
  }

  @OnClick(R.id.answer1) public void answer1_onClick() {
    selectAnswer(1);
  }

  @OnClick(R.id.answer2) public void answer2_onClick() {
    selectAnswer(2);
  }

  @OnClick(R.id.answer3) public void answer3_onClick() {
    selectAnswer(3);
  }

  private void selectAnswer(int answer) {
    if (!interactor.getAllowClickingAnswers()) return;

    int currentAnswer = interactor.getChosenAnswer(questionIdx);
    if (currentAnswer != 0 && interactor.getShouldRevealAnswersImmediately()) return;

    interactor.selectAnswer(answer);

    if (currentAnswer == answer) {
      highlightAnswer(0);
    } else {
      highlightAnswer(answer);
      if (!interactor.getShouldRevealAnswersImmediately()) {
        interactor.nextQuestion();
      }
    }
  }

  /**
   * Highlight the appropriate buttons.
   *
   * @param answer The index of the button that was pressed, from 1 to 3.
   */
  public void highlightAnswer(int answer) {
    List<AppCompatButton> buttons = new ArrayList<>();
    buttons.add(answer_button_1);
    buttons.add(answer_button_2);
    buttons.add(answer_button_3);

    Context ctx = getActivity();
    int colorNormal = resolveColorAttr(ctx, R.attr.answerColorNormal);
    int colorSelected = resolveColorAttr(ctx, R.attr.answerColorSelected);
    int colorCorrect = resolveColorAttr(ctx, R.attr.answerColorCorrect);
    int colorIncorrect = resolveColorAttr(ctx, R.attr.answerColorIncorrect);
    int colorNormalText = resolveColorAttr(ctx, R.attr.answerTextColorNormal);
    int colorCorrectText = resolveColorAttr(ctx, R.attr.answerTextColorCorrect);

    // Change all buttons color to normal.
    for (AppCompatButton button : buttons) {
      colorButton(button, colorNormal, colorNormalText);
    }

    if (!interactor.getCompleted()
        && answer == 0
        && interactor.getShouldRevealAnswersImmediately()) {
      return;
    }

    if (interactor.getTestType() == TestTypes.NORMAL
        || interactor.getTestType() == TestTypes.HISTORY) {
      if (interactor.getCompleted() || interactor.getShouldRevealAnswersImmediately()) {
        if (answer == 0) {
          // color correct gray
          colorButton(buttons.get(interactor.getCorrectAnswer(questionIdx) - 1), colorSelected,
              colorNormalText);
        } else {
          // always color correctAnswer green
          colorButton(buttons.get(interactor.getCorrectAnswer(questionIdx) - 1), colorCorrect,
              colorCorrectText);

          if (answer != interactor.getCorrectAnswer(questionIdx)) {
            // color incorrectAnswer red
            colorButton(buttons.get(answer - 1), colorIncorrect, colorNormalText);
          }
        }
      } else {
        if (answer != 0) {
          // color answer gray
          colorButton(buttons.get(answer - 1), colorSelected, colorNormalText);
        }
      }
    } else if (interactor.getTestType() == TestTypes.CORRECT_ANSWERS) {
      // just color correct green every time. This should probably be handle by some different function, some that doesn't take Answer as an argument.
      colorButton(buttons.get(interactor.getCorrectAnswer(questionIdx) - 1), colorCorrect,
          colorCorrectText);
    }
  }

  private void colorButton(AppCompatButton button, int color, int textColor) {
    if (button != null) {
      tintAnswerButton(button, color);
      button.setTextColor(textColor);
    }
  }

  private void tintAnswerButton(AppCompatButton button, int color) {
    if (SDK_INT >= LOLLIPOP) {
      button.getBackground().setColorFilter(color, MULTIPLY);
    } else {
      int[][] states = new int[][] { new int[] { android.R.attr.state_enabled } };
      int[] colors = new int[] { color };
      final ColorStateList backgroundTintList = new ColorStateList(states, colors);
      ViewCompat.setBackgroundTintList(button, backgroundTintList);
    }
  }

  public void scrollToTop() {
    scroll_view.post(new Runnable() {
      @Override public void run() {
        scroll_view.fullScroll(ScrollView.FOCUS_UP);
      }
    });
  }

  public void setQuestionText(String questionText) {
    question_text.setText(questionText);
  }

  /**
   * Show or hide the image view based on question type.
   * Types: 0 - text only, 1 - road sign, 2 - intersection
   */
  public void setQuestionImage(Drawable drawable) {
    if (drawable != null) {
      int type = interactor.getQuestionType(questionIdx);

      if (type == 0) {
        //top_space.setVisibility(View.VISIBLE);
      } else if (type == 1) {
        question_image.setImageDrawable(drawable);
        question_image.setVisibility(View.VISIBLE);
        intersection_image.setVisibility(View.GONE);
        //intersection_canvas.setVisibility(View.GONE);
        //top_space.setVisibility(View.VISIBLE);
      } else if (type == 2) {
        intersection_image.setImageDrawable(drawable);
        intersection_image.setVisibility(View.VISIBLE);
        //intersection_canvas.clearCanvas();
        //intersection_canvas.setVisibility(View.VISIBLE);
        question_image.setVisibility(View.GONE);
        //top_space.setVisibility(View.GONE);
      } else {
      }
    } else {
      question_image.setVisibility(View.GONE);
      intersection_image.setVisibility(View.GONE);
    }
  }
}