import { ReactWrapper, mount } from 'enzyme';
import * as React from 'react';

import { ItemEssayFormProps, ItemEssayFormState } from './ItemEssayForm';
import { ItemType, ItemEssayConfig } from '../../../../../../modules/api/sandalphon/problemBundle';
import { AnswerState } from '../../../../../../components/ProblemWorksheetCard/Bundle/itemStatement';
import ItemEssayForm from './ItemEssayForm';

describe('ItemEssayForm', () => {
  let wrapper: ReactWrapper<ItemEssayFormProps, ItemEssayFormState>;
  const onSubmitFn: jest.Mocked<any> = jest.fn(() => Promise.resolve(true));
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

      it('should be only 1 button', () => {
        const button = wrapper.find('button');
        expect(button.length).toEqual(1);
        expect(button.text()).toEqual('Answer');
      });

      test("helptext should be 'No answered.'", () => {
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
        const textarea = wrapper.find('textarea');
        textarea.simulate('change', { target: { value: 'answer' } });
      });

      test("textarea value should be 'answer'", () => {
        const value = wrapper.find('textarea').props().value;
        expect(value).toEqual('answer');
      });

      test('cancel filling the answer should render 1 button', async () => {
        const cancelButton = wrapper.find('button').last();
        await cancelButton.simulate('click');
        const button = wrapper.find('button');
        expect(button.length).toEqual(1);
        expect(button.text()).toEqual('Answer');
        expect(onSubmitFn).not.toBeCalled();
      });

      test('submit the answer', async () => {
        const prevHelpText = wrapper.find('div').last();
        const submitButton = wrapper.find('button').first();
        await submitButton.simulate('submit');
        const helpText = wrapper.find('div').last();
        expect(helpText).not.toEqual(prevHelpText);
        expect(onSubmitFn).toBeCalled();
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

      it('should render two buttons', () => {
        const button = wrapper.find('button');
        const buttonText = button.map(bt => bt.text());
        expect(button.length).toEqual(2);
        expect(buttonText).toEqual(['Change', 'Clear']);
      });

      test("helptext should be 'Answered'", () => {
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

      test("textarea value should be 'answer'", () => {
        const value = wrapper.find('textarea').props().value;
        expect(value).toEqual('answer');
      });

      test('cancel filling the answer should render 2 buttons', async () => {
        const cancelButton = wrapper.find('button').last();
        await cancelButton.simulate('click');
        const button = wrapper.find('button');
        const buttonText = button.map(bt => bt.text());
        expect(button.length).toEqual(2);
        expect(buttonText).toEqual(['Change', 'Clear']);
      });

      test('submit the answer', async () => {
        const prevHelpText = wrapper.find('div').last();
        const submitButton = wrapper.find('button').first();
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
    const props: ItemEssayFormProps = {
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

    it('buttons disabled', () => {
      const button = wrapper.find('button');
      const buttonsDisabled = button.map(bt => bt.props().disabled);
      expect(button.length).toBeGreaterThan(1);
      expect(buttonsDisabled).toEqual([true, true]);
    });
  });
});
