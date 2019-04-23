import ItemShortAnswerForm, { ItemShortAnswerFormProps, ItemShortAnswerFormState } from './ItemShortAnswerForm';
import { ReactWrapper, mount } from 'enzyme';
import { ItemShortAnswerConfig, ItemType } from 'modules/api/sandalphon/problemBundle';
import { AnswerState } from 'components/ProblemWorksheetCard/Bundle/itemStatement';
import * as React from 'react';
describe('ItemShortAnswerForm', () => {
  let wrapper: ReactWrapper<ItemShortAnswerFormProps, ItemShortAnswerFormState>;
  const onSubmitFn: jest.Mocked<any> = jest.fn();
  const itemConfig: ItemShortAnswerConfig = {
    statement: 'statement',
    score: 4,
    penalty: -2,
    inputValidationRegex: '/^d+$/',
    gradingRegex: '/^d+$/',
  };

  beforeEach(() => {
    window.confirm = jest.fn(() => true);
  });

  describe('not answered yet condition', () => {
    const props: ItemShortAnswerFormProps = {
      jid: 'jid',
      type: ItemType.ShortAnswer,
      meta: 'meta',
      config: itemConfig,
      disabled: false,
      onSubmit: onSubmitFn,
      answerState: AnswerState.NotAnswered,
    };

    beforeEach(() => {
      wrapper = mount(<ItemShortAnswerForm {...props} />);
    });

    test('text input should has empty value', () => {
      const textInputValue = wrapper.find('input').props().value;
      expect(textInputValue).toEqual('');
    });

    it('should render 1 button', () => {
      const button = wrapper.find('button');
      expect(button.length).toEqual(1);
      expect(button.text()).toEqual('Answer');
    });

    test("helptext should be 'Not answered.'", () => {
      const div = wrapper.find('div');
      const helpText = div.at(div.length - 2).text();
      expect(helpText).toContain('Not answered.');
    });

    describe('fill and submit the answer', () => {
      beforeEach(async () => {
        const answerButton = wrapper.find('button');
        await answerButton.simulate('submit');
        const textInput = wrapper.find('input');
        textInput.simulate('change', { target: { value: '1' } });
      });

      test('fill the answer with wrong format will render the new help text', () => {
        const textInput = wrapper.find('input');
        textInput.simulate('change', { target: { value: 'answer' } });
        const helpText = wrapper
          .find('div')
          .at(1)
          .text();
        expect(helpText).toContain('Wrong answer format!');
      });

      test('cancel answer should render 1 button', async () => {
        const cancelButton = wrapper.find('button').last();
        await cancelButton.simulate('click');
        const button = wrapper.find('button');
        expect(button.length).toEqual(1);
        expect(button.text()).toEqual('Answer');
      });

      test('submit the answer', async () => {
        const prevHelpText = wrapper.find('div').at(1);
        const submitButton = wrapper.find('button').first();
        await submitButton.simulate('submit');
        const helpText = wrapper.find('div').at(1);
        expect(helpText).not.toEqual(prevHelpText);
        expect(onSubmitFn).toBeCalled();
      });
    });
  });

  describe('answered', () => {
    const props: ItemShortAnswerFormProps = {
      jid: 'jid',
      type: ItemType.ShortAnswer,
      meta: 'meta',
      config: itemConfig,
      disabled: false,
      initialAnswer: '1',
      onSubmit: onSubmitFn,
      answerState: AnswerState.AnswerSaved,
    };

    beforeEach(() => {
      wrapper = mount(<ItemShortAnswerForm {...props} />);
    });

    it('should render text input with initial answer', () => {
      const textInputValue = wrapper.find('input').props().value;
      expect(textInputValue).toEqual(props.initialAnswer);
    });

    it('should render two buttons', () => {
      const button = wrapper.find('button');
      const buttonTexts = button.map(bt => bt.text());
      expect(button.length).toEqual(2);
      expect(buttonTexts).toEqual(['Change', 'Clear']);
    });

    describe('change the answer', () => {
      beforeEach(async () => {
        const changeButton = wrapper.find('button').first();
        await changeButton.simulate('submit');
        const textInput = wrapper.find('input');
        textInput.simulate('change', { target: { value: '2' } });
      });

      test('change the answer with right format', () => {
        const state = wrapper.state();
        expect(state.answer).toEqual('2');
      });

      test('change the answer with wrong format', () => {
        const textInput = wrapper.find('input');
        textInput.simulate('change', { target: { value: 'answer' } });
        const helpText = wrapper
          .find('div')
          .at(1)
          .text();
        expect(helpText).toContain('Wrong answer format!');
      });

      test('cancel answer should render new buttons', async () => {
        const prevButtons = wrapper.find('button');
        const cancelButton = prevButtons.last();
        await cancelButton.simulate('click');
        const buttons = wrapper.find('button');
        const buttonTexts = buttons.map(bt => bt.text());
        expect(buttons).not.toEqual(prevButtons);
        expect(buttonTexts).toEqual(['Change', 'Clear']);
      });

      test('submit the answer', async () => {
        const prevHelpText = wrapper.find('div').at(1);
        const submitButton = wrapper.find('button').first();
        await submitButton.simulate('submit');
        const helpText = wrapper.find('div').at(1);
        expect(onSubmitFn).toBeCalled();
        expect(helpText).not.toEqual(prevHelpText);
      });
    });

    test('clear the answer', async () => {
      const prevButtons = wrapper.find('button');
      const clearButton = prevButtons.last();
      await clearButton.simulate('click');
      const button = wrapper.find('button');
      expect(button).not.toEqual(prevButtons);
    });
  });

  describe('disabled', () => {
    const props: ItemShortAnswerFormProps = {
      jid: 'jid',
      type: ItemType.ShortAnswer,
      meta: 'meta',
      config: itemConfig,
      disabled: true,
      initialAnswer: '1',
      onSubmit: onSubmitFn,
      answerState: AnswerState.AnswerSaved,
    };

    beforeEach(() => {
      wrapper = mount(<ItemShortAnswerForm {...props} />);
    });

    it('buttons disabled', () => {
      const button = wrapper.find('button');
      const buttonsDisabled = button.map(bt => bt.props().disabled);
      expect(button.length).toBeGreaterThan(1);
      expect(buttonsDisabled).toEqual([true, true]);
    });
  });
});
