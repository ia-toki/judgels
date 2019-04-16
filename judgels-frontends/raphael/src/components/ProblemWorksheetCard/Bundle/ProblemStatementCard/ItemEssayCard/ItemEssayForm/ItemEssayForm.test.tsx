import { ReactWrapper, mount } from 'enzyme';
import { ItemEssayFormProps, ItemEssayFormState } from './ItemEssayForm';
import { ItemType, ItemEssayConfig } from 'modules/api/sandalphon/problemBundle';
import { AnswerState } from 'components/ProblemWorksheetCard/Bundle/itemStatement';
import ItemEssayForm from './ItemEssayForm';
import * as React from 'react';

describe('ItemEssayForm', () => {
  let wrapper: ReactWrapper<ItemEssayFormProps, ItemEssayFormState>;
  const itemConfig: ItemEssayConfig = {
    statement: 'statement',
    score: 100,
  };

  beforeEach(() => {
    window.confirm = jest.fn(() => true);
  });

  describe('not answered yet', () => {
    const props: ItemEssayFormProps = {
      jid: 'jid',
      type: ItemType.Essay,
      meta: 'meta',
      config: itemConfig,
      disabled: false,
      onSubmit: jest.fn().mockReturnValue(undefined),
      answerState: AnswerState.NotAnswered,
    };

    beforeEach(() => {
      wrapper = mount(<ItemEssayForm {...props} />);
    });

    describe('initial condition', () => {
      it('initial state', () => {
        const state = wrapper.state();
        const expectedState = {
          answerState: props.answerState,
          answer: '',
          initialAnswer: '',
          cancelButtonState: AnswerState.NotAnswered,
        };
        expect(state).toEqual(expectedState);
      });

      it('textarea', () => {
        const textareaValue = wrapper.find('textarea').props().value;
        expect(textareaValue).toEqual('');
      });

      it('only answer button', () => {
        const button = wrapper.find('button');
        expect(button.length).toEqual(1);
      });

      it('button text', () => {
        const buttonText = wrapper.find('button').text();
        expect(buttonText).toEqual('Answer');
      });

      it('no helptext', () => {
        const helpText = wrapper
          .find('div')
          .last()
          .text();
        expect(helpText).toContain('Not answered.');
      });
    });

    describe('fill and submit the answer', () => {
      beforeEach(async () => {
        const answerButton = wrapper.find('button').first();
        await answerButton.simulate('submit');
        const textarea = wrapper.find('textarea').first();
        textarea.simulate('change', { target: { value: 'answer' } });
      });

      it('fill the answer', () => {
        const state = wrapper.state();
        expect(state.answer).toEqual('answer');
        expect(state.answerState).toEqual(AnswerState.Answering);
      });

      it('cancel answer', async () => {
        const cancelButton = wrapper.find('button').last();
        await cancelButton.simulate('click');
        const state = wrapper.state();
        const expectedState = {
          answerState: AnswerState.NotAnswered,
          answer: '',
          initialAnswer: '',
          cancelButtonState: AnswerState.NotAnswered,
        };
        expect(state).toEqual(expectedState);
      });

      it('submit the answer', async () => {
        const submitButton = wrapper.find('button').first();
        await submitButton.simulate('submit');
        const state = wrapper.state();
        const expectedState = {
          answerState: AnswerState.AnswerSaved,
          answer: 'answer',
          initialAnswer: 'answer',
          cancelButtonState: AnswerState.AnswerSaved,
        };
        expect(state).toEqual(expectedState);
      });
    });
  });

  describe('answered', () => {
    const props: ItemEssayFormProps = {
      jid: 'jid',
      type: ItemType.Essay,
      meta: 'meta',
      config: itemConfig,
      disabled: false,
      initialAnswer: 'initial',
      onSubmit: jest.fn().mockReturnValue(undefined),
      answerState: AnswerState.AnswerSaved,
    };

    beforeEach(() => {
      wrapper = mount(<ItemEssayForm {...props} />);
    });

    describe('initial condition', () => {
      it('initial state', () => {
        const state = wrapper.state();
        const expectedState = {
          answerState: props.answerState,
          answer: props.initialAnswer,
          initialAnswer: props.initialAnswer,
          cancelButtonState: AnswerState.AnswerSaved,
        };
        expect(state).toEqual(expectedState);
      });

      it('textarea', () => {
        const textareaValue = wrapper.find('textarea').props().value;
        expect(textareaValue).toEqual(props.initialAnswer);
      });

      it('not only answer button', () => {
        const button = wrapper.find('button');
        expect(button.length).toBeGreaterThan(1);
      });

      it('button text', () => {
        const buttonTexts = wrapper.find('button').map(button => button.text());
        expect(buttonTexts).toEqual(['Change', 'Clear']);
      });

      it('helptext', () => {
        const helpText = wrapper
          .find('div')
          .last()
          .text();
        expect(helpText).toContain('Answered.');
      });
    });

    describe('change the answer', () => {
      beforeEach(async () => {
        const changeButton = wrapper.find('button').first();
        await changeButton.simulate('submit');
        const textarea = wrapper.find('textarea').first();
        textarea.simulate('change', { target: { value: 'answer' } });
      });

      it('fill the answer', () => {
        const state = wrapper.state();
        expect(state.answer).toEqual('answer');
        expect(state.answerState).toEqual(AnswerState.Answering);
      });

      it('cancel answer', async () => {
        const cancelButton = wrapper.find('button').last();
        await cancelButton.simulate('click');
        const state = wrapper.state();
        const expectedState = {
          answerState: props.answerState,
          answer: props.initialAnswer,
          initialAnswer: props.initialAnswer,
          cancelButtonState: AnswerState.AnswerSaved,
        };
        expect(state).toEqual(expectedState);
      });

      it('submit the answer', async () => {
        const submitButton = wrapper.find('button').first();
        await submitButton.simulate('submit');
        const state = wrapper.state();
        const expectedState = {
          answerState: AnswerState.AnswerSaved,
          answer: 'answer',
          initialAnswer: 'answer',
          cancelButtonState: AnswerState.AnswerSaved,
        };
        expect(state).toEqual(expectedState);
      });
    });

    it('clear the answer', async () => {
      const clearButton = wrapper.find('button').last();
      await clearButton.simulate('click');
      const state = wrapper.state();
      const expectedState = {
        answerState: AnswerState.NotAnswered,
        answer: '',
        initialAnswer: '',
        cancelButtonState: AnswerState.NotAnswered,
      };
      expect(window.confirm).toBeCalled();
      expect(state).toEqual(expectedState);
    });
  });
});
