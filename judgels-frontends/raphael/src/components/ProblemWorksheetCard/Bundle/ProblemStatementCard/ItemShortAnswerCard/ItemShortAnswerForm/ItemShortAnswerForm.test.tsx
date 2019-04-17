import ItemShortAnswerForm, { ItemShortAnswerFormProps, ItemShortAnswerFormState } from './ItemShortAnswerForm';
import { ReactWrapper, mount } from 'enzyme';
import { ItemShortAnswerConfig, ItemType } from 'modules/api/sandalphon/problemBundle';
import { AnswerState } from 'components/ProblemWorksheetCard/Bundle/itemStatement';
import * as React from 'react';
describe('ItemShortAnswerForm', () => {
  let wrapper: ReactWrapper<ItemShortAnswerFormProps, ItemShortAnswerFormState>;
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

  describe('not answered yet', () => {
    const props: ItemShortAnswerFormProps = {
      jid: 'jid',
      type: ItemType.ShortAnswer,
      meta: 'meta',
      config: itemConfig,
      disabled: false,
      onSubmit: jest.fn().mockReturnValue(undefined),
      answerState: AnswerState.NotAnswered,
    };

    beforeEach(() => {
      wrapper = mount(<ItemShortAnswerForm {...props} />);
    });

    describe('initial condition', () => {
      it('initial state', () => {
        const state = wrapper.state();
        const expectedState = {
          answerState: props.answerState,
          initialAnswer: '',
          answer: '',
          cancelButtonState: AnswerState.NotAnswered,
          previousWrongFormat: true,
          wrongFormat: true,
        };
        expect(state).toEqual(expectedState);
      });

      it('text input', () => {
        const textInputValue = wrapper.find('input').props().value;
        expect(textInputValue).toEqual('');
      });

      it('only answer button', () => {
        const button = wrapper.find('button');
        expect(button.length).toEqual(1);
        expect(button.text()).toEqual('Answer');
      });

      it('helptext', () => {
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

        it('fill the answer with right format', () => {
          const state = wrapper.state();
          expect(state.answer).toEqual('1');
          expect(state.answerState).toEqual(AnswerState.Answering);
        });

        it('fill the answer with wrong format', () => {
          const textInput = wrapper.find('input');
          textInput.simulate('change', { target: { value: 'answer' } });
          const state = wrapper.state();
          const div = wrapper.find('div');
          const helpText = div.at(1).text();
          expect(helpText).toContain('Wrong answer format!');
          expect(state.answer).toEqual('answer');
          expect(state.wrongFormat).toEqual(true);
        });

        it('cancel answer', async () => {
          const cancelButton = wrapper.find('button').last();
          await cancelButton.simulate('click');
          const state = wrapper.state();
          const expectedState = {
            answerState: props.answerState,
            initialAnswer: '',
            answer: '',
            cancelButtonState: AnswerState.NotAnswered,
            previousWrongFormat: true,
            wrongFormat: true,
          };
          expect(state).toEqual(expectedState);
        });

        it('submit the answer', async () => {
          const submitButton = wrapper.find('button').first();
          await submitButton.simulate('submit');
          const state = wrapper.state();
          const expectedState = {
            answerState: AnswerState.AnswerSaved,
            initialAnswer: '1',
            answer: '1',
            cancelButtonState: AnswerState.AnswerSaved,
            previousWrongFormat: true,
            wrongFormat: true,
          };
          expect(state).toEqual(expectedState);
        });
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
      onSubmit: jest.fn().mockReturnValue(undefined),
      answerState: AnswerState.AnswerSaved,
    };

    beforeEach(() => {
      wrapper = mount(<ItemShortAnswerForm {...props} />);
    });

    describe('initial condition', () => {
      it('initial state', () => {
        const state = wrapper.state();
        const expectedState = {
          answerState: props.answerState,
          initialAnswer: props.initialAnswer,
          answer: props.initialAnswer,
          cancelButtonState: AnswerState.AnswerSaved,
          previousWrongFormat: false,
          wrongFormat: false,
        };
        expect(state).toEqual(expectedState);
      });

      it('text input', () => {
        const textInputValue = wrapper.find('input').props().value;
        expect(textInputValue).toEqual(props.initialAnswer);
      });

      it('not only answer button', () => {
        const button = wrapper.find('button');
        const buttonTexts = button.map(bt => bt.text());
        expect(button.length).toBeGreaterThan(1);
        expect(buttonTexts).toEqual(['Change', 'Clear']);
      });

      it('helptext', () => {
        const div = wrapper.find('div');
        const helpText = div.at(div.length - 2).text();
        expect(helpText).toContain('Answered.');
      });
    });

    describe('change the answer', () => {
      beforeEach(async () => {
        const changeButton = wrapper.find('button').first();
        await changeButton.simulate('submit');
        const textInput = wrapper.find('input');
        textInput.simulate('change', { target: { value: '2' } });
      });

      it('change the answer with right format', () => {
        const state = wrapper.state();
        expect(state.answer).toEqual('2');
        expect(state.answerState).toEqual(AnswerState.Answering);
      });

      it('change the answer with wrong format', () => {
        const textInput = wrapper.find('input');
        textInput.simulate('change', { target: { value: 'answer' } });
        const state = wrapper.state();
        const div = wrapper.find('div');
        const helpText = div.at(1).text();
        expect(helpText).toContain('Wrong answer format!');
        expect(state.answer).toEqual('answer');
        expect(state.wrongFormat).toEqual(true);
      });

      it('cancel answer', async () => {
        const cancelButton = wrapper.find('button').last();
        await cancelButton.simulate('click');
        const state = wrapper.state();
        const expectedState = {
          answerState: props.answerState,
          initialAnswer: props.initialAnswer,
          answer: props.initialAnswer,
          cancelButtonState: AnswerState.AnswerSaved,
          previousWrongFormat: false,
          wrongFormat: false,
        };
        expect(state).toEqual(expectedState);
      });

      it('submit the answer', async () => {
        const submitButton = wrapper.find('button').first();
        await submitButton.simulate('submit');
        const state = wrapper.state();
        const expectedState = {
          answerState: AnswerState.AnswerSaved,
          initialAnswer: '2',
          answer: '2',
          cancelButtonState: AnswerState.AnswerSaved,
          previousWrongFormat: true,
          wrongFormat: true,
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
        initialAnswer: '',
        answer: '',
        cancelButtonState: AnswerState.NotAnswered,
        previousWrongFormat: true,
        wrongFormat: true,
      };
      expect(state).toEqual(expectedState);
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
      onSubmit: jest.fn().mockReturnValue(undefined),
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
