import { mount } from 'enzyme';
import { act } from 'react-dom/test-utils';

import { ItemType } from '../../../../../../modules/api/sandalphon/problemBundle';
import { AnswerState } from '../../../itemStatement';
import ItemEssayForm from './ItemEssayForm';

describe('ItemEssayForm', () => {
  let wrapper;
  const onSubmitFn = jest.fn(() => Promise.resolve(true));
  const itemConfig = {
    statement: 'statement',
    score: 100,
  };

  beforeEach(() => {
    window.confirm = jest.fn(() => true);
  });

  describe('not answered yet', () => {
    const props = {
      jid: 'jid',
      type: ItemType.Essay,
      meta: 'meta',
      config: itemConfig,
      disabled: false,
      onSubmit: onSubmitFn,
      answerState: AnswerState.NotAnswered,
    };

    beforeEach(() => {
      wrapper = mount(<ItemEssayForm {...props} />);
    });

    describe('initial condition', () => {
      it('should render textarea with empty value', () => {
        const textareaValue = wrapper.find('textarea').props().value;
        expect(textareaValue).toEqual('');
      });

      it('should be no buttons', () => {
        const buttons = wrapper.find('button');
        expect(buttons.length).toEqual(0);
      });

      test("helptext should be 'Unanswered.'", () => {
        const helpText = wrapper.find('div').last().text();
        expect(helpText).toContain('Unanswered.');
      });
    });

    describe('fill and submit the answer', () => {
      beforeEach(() => {
        const answerButton = wrapper.find('form');
        answerButton.simulate('submit');
        const textarea = wrapper.find('textarea');
        act(() => {
          textarea.prop('onChange')({ target: { value: 'answer' } });
        });
        wrapper.update();
      });

      test("textarea value should be 'answer'", () => {
        const value = wrapper.find('textarea').props().value;
        expect(value).toEqual('answer');
      });

      test('cancel filling the answer should render no buttons', () => {
        const cancelButton = wrapper.find('button').last();
        cancelButton.simulate('click');
        const buttons = wrapper.find('button');
        expect(buttons.length).toEqual(0);
      });

      test('submit the answer', () => {
        const prevHelpText = wrapper.find('div').last();
        const submitButton = wrapper.find('form');
        submitButton.simulate('submit');
        const helpText = wrapper.find('div').last();
        expect(helpText).not.toEqual(prevHelpText);
        expect(onSubmitFn).toBeCalled();
      });
    });
  });

  describe('answered', () => {
    const props = {
      jid: 'jid',
      type: ItemType.Essay,
      meta: 'meta',
      config: itemConfig,
      disabled: false,
      initialAnswer: 'initial',
      onSubmit: onSubmitFn,
      answerState: AnswerState.AnswerSaved,
    };

    beforeEach(() => {
      wrapper = mount(<ItemEssayForm {...props} />);
    });

    describe('initial condition', () => {
      it('should render textarea with initial answer', () => {
        const textareaValue = wrapper.find('textarea').props().value;
        expect(textareaValue).toEqual(props.initialAnswer);
      });

      it('should render Clear button', () => {
        const button = wrapper.find('button');
        expect(button.text()).toEqual('Clear');
      });

      test("helptext should be 'Answered'", () => {
        const helpText = wrapper.find('div').last().text();
        expect(helpText).toContain('Answered.');
      });
    });

    describe('change the answer', () => {
      beforeEach(async () => {
        const changeButton = wrapper.find('form');
        await changeButton.simulate('submit');
        const textarea = wrapper.find('textarea').first();
        act(() => {
          textarea.prop('onChange')({ target: { value: 'answer' } });
        });
        wrapper.update();
      });

      test("textarea value should be 'answer'", () => {
        const value = wrapper.find('textarea').props().value;
        expect(value).toEqual('answer');
      });

      test('cancel filling the answer should render Clear button', async () => {
        const cancelButton = wrapper.find('button').last();
        await cancelButton.simulate('click');
        const button = wrapper.find('button');
        expect(button.text()).toEqual('Clear');
      });

      test('submit the answer', async () => {
        const prevHelpText = wrapper.find('div').last();
        const submitButton = wrapper.find('form');
        await submitButton.simulate('submit');
        const helpText = wrapper.find('div').last();
        expect(helpText).not.toEqual(prevHelpText);
        expect(onSubmitFn).toBeCalled();
      });
    });

    test('clear the answer', async () => {
      const prevHelpText = wrapper.find('div').last();
      const clearButton = wrapper.find('button').last();
      await clearButton.simulate('click');
      const helpText = wrapper.find('div').last();
      expect(helpText).not.toEqual(prevHelpText);
      expect(window.confirm).toBeCalled();
    });
  });

  describe('disabled', () => {
    const props = {
      jid: 'jid',
      type: ItemType.Essay,
      meta: 'meta',
      config: itemConfig,
      disabled: true,
      initialAnswer: 'initial',
      onSubmit: jest.fn().mockReturnValue(undefined),
      answerState: AnswerState.AnswerSaved,
    };

    beforeEach(() => {
      wrapper = mount(<ItemEssayForm {...props} />);
    });

    it('button disabled', () => {
      const button = wrapper.find('button');
      expect(button.props().disabled).toEqual(true);
    });
  });
});
