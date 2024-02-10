import { mount } from 'enzyme';
import { Provider } from 'react-redux';
import createMockStore from 'redux-mock-store';

import { ItemType } from '../../../../../modules/api/sandalphon/problemBundle';
import { ItemMultipleChoiceCard } from './ItemMultipleChoiceCard';

describe('ItemMultipleChoiceCard', () => {
  let wrapper;
  const onChoiceChangeFn = jest.fn();
  const itemConfig = {
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
  const multipleChoiceCardProps = {
    jid: 'jid',
    type: ItemType.MultipleChoice,
    meta: 'meta',
    config: itemConfig,
    disabled: false,
    onChoiceChange: onChoiceChangeFn,
    itemNumber: 1,
  };

  beforeEach(() => {
    const store = createMockStore()({});
    const props = multipleChoiceCardProps;
    wrapper = mount(
      <Provider store={store}>
        <ItemMultipleChoiceCard {...props} />
      </Provider>
    );
  });

  test('Answer the question by clicking a radio button', () => {
    const radioButton = wrapper.find('label').children().find('input').first();
    radioButton.getDOMNode().checked = true;
    radioButton.simulate('change');
    expect(onChoiceChangeFn).toBeCalled();
  });

  test('Answer the question and change the answer', () => {
    const prevAnswer = wrapper.find('label').children().find('input').first();
    prevAnswer.getDOMNode().checked = true;
    prevAnswer.simulate('click');
    const currentAnswer = wrapper.find('label').children().find('input').last();
    currentAnswer.getDOMNode().checked = true;
    currentAnswer.simulate('change');
    expect(onChoiceChangeFn).toHaveBeenCalled();
  });
});
