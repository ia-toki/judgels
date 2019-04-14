import {
  ItemMultipleChoiceCardProps,
  ItemMultipleChoiceCard,
  ItemMultipleChoiceCardState,
} from './ItemMultipleChoiceCard';
import { ReactWrapper, mount } from 'enzyme';
import { ItemType, ItemMultipleChoiceConfig } from 'modules/api/sandalphon/problemBundle';
import * as React from 'react';

describe('ItemMultipleChoiceCard', () => {
  let wrapper: ReactWrapper<ItemMultipleChoiceCardProps>;
  const itemConfig: ItemMultipleChoiceConfig = {
    statement: 'Statement',
    choices: [
      {
        alias: 'A',
        content: 'A',
      },
      {
        alias: 'B',
        content: 'B',
      },
      {
        alias: 'C',
        content: 'C',
      },
    ],
  };
  const multipleChoiceCardProps: ItemMultipleChoiceCardProps = {
    jid: 'jid',
    type: ItemType.MultipleChoice,
    meta: 'meta',
    config: itemConfig,
    disabled: false,
    onChoiceChange: jest.fn(),
    itemNumber: 1,
  };

  beforeEach(() => {
    const props = multipleChoiceCardProps;
    wrapper = mount(<ItemMultipleChoiceCard {...props} />);
  });

  it('Answer the question by clicking a radio button', () => {
    const radioButton = wrapper
      .find('label')
      .children()
      .find('input')
      .first();
    radioButton.simulate('change');
    const state: ItemMultipleChoiceCardState = wrapper.state();
    expect(state.value).toEqual('A');
  });

  it('Answer the question and change the answer', () => {
    const prevAnswer = wrapper
      .find('label')
      .children()
      .find('input')
      .first();
    prevAnswer.simulate('change');
    const prevState = (wrapper.state() as ItemMultipleChoiceCardState).value;
    const currentAnswer = wrapper
      .find('label')
      .children()
      .find('input')
      .last();
    currentAnswer.simulate('change');
    const currentState = (wrapper.state() as ItemMultipleChoiceCardState).value;
    const state = [prevState, currentState];
    expect(state).toEqual(['A', 'C']);
  });
});
