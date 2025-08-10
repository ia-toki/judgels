import { mount } from 'enzyme';
import { act } from 'react-dom/test-utils';

import { ItemType } from '../../../../../../modules/api/sandalphon/problemBundle';
import { AnswerState } from '../../../itemStatement';
import ItemShortAnswerForm from './ItemShortAnswerForm';

describe('ItemShortAnswerForm', () => {
  let wrapper;
  const onSubmitFn = jest.fn();
  const itemConfig = {
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
    const props = {
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

    it('should render no buttons', () => {
      const buttons = wrapper.find('button');
      expect(buttons.length).toEqual(0);
    });

    test("helptext should be 'Unanswered.'", () => {
      const div = wrapper.find('div');
      const helpText = div.at(div.length - 2).text();
      expect(helpText).toContain('Unanswered.');
    });

    describe('fill and submit the answer', () => {
      beforeEach(() => {
        const answerButton = wrapper.find('form');
        answerButton.simulate('submit');

        const textInput = wrapper.find('input');
        act(() => {
          textInput.prop('onChange')({ target: { value: '1' } });
        });
      });

      test('fill the answer with wrong format will render the new help text', () => {
        const textInput = wrapper.find('input');
        textInput.prop('onChange')({ target: { value: 'answer' } });
        wrapper.update();
        const helpText = wrapper.find('div').at(1).text();
        expect(helpText).toContain('Wrong answer format!');
      });

      test('cancel answer should render no buttons', async () => {
        const cancelButton = wrapper.find('button').last();
        await cancelButton.simulate('click');
        const buttons = wrapper.find('button');
        expect(buttons.length).toEqual(0);
      });

      test('submit the answer', () => {
        const prevHelpText = wrapper.find('div').at(1);
        const submitButton = wrapper.find('form');
        submitButton.simulate('submit');
        const helpText = wrapper.find('div').at(1);
        expect(helpText).not.toEqual(prevHelpText);
        expect(onSubmitFn).toBeCalled();
      });
    });
  });

  describe('answered', () => {
    const props = {
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

    it('should render Clear button', () => {
      const button = wrapper.find('button');
      expect(button.length).toEqual(1);
      expect(button.text()).toEqual('Clear');
    });

    describe('change the answer', () => {
      beforeEach(() => {
        const textInput = wrapper.find('input');
        textInput.simulate('click');
        act(() => {
          textInput.prop('onChange')({ target: { value: '2' } });
        });
      });

      test('change the answer with right format', () => {
        const state = wrapper.state();
        expect(state.answer).toEqual('2');
      });

      test('change the answer with wrong format', () => {
        const textInput = wrapper.find('input');
        textInput.prop('onChange')({ target: { value: 'answer' } });
        wrapper.update();
        const helpText = wrapper.find('div').at(1).text();
        expect(helpText).toContain('Wrong answer format!');
      });

      test('cancel answer should render new Clear button', () => {
        const prevButtons = wrapper.find('button');
        const cancelButton = prevButtons.last();
        cancelButton.simulate('click');
        const clearButton = wrapper.find('button');
        expect(clearButton).not.toEqual(prevButtons);
        expect(clearButton.text()).toEqual('Clear');
      });

      test('submit the answer', () => {
        const prevHelpText = wrapper.find('div').at(1);
        const submitButton = wrapper.find('form');
        submitButton.simulate('submit');
        const helpText = wrapper.find('div').at(1);
        expect(onSubmitFn).toBeCalled();
        expect(helpText).not.toEqual(prevHelpText);
      });
    });

    test('clear the answer', () => {
      const clearButton = wrapper.find('button');
      clearButton.simulate('click');
      const button = wrapper.find('button');
      expect(button).not.toEqual(clearButton);
    });
  });

  describe('disabled', () => {
    const props = {
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
      expect(button.props().disabled).toEqual(true);
    });
  });
});
