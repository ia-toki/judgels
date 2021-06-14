import { mount } from 'enzyme';
import { Provider } from 'react-redux';
import { combineReducers, createStore } from 'redux';
import { reducer as formReducer } from 'redux-form';

import { ContestClarificationAnswerBox } from './ContestClarificationAnswerBox';

describe('ContestClarificationAnswerBox', () => {
  let onAnswerClarification;
  let wrapper;

  beforeEach(() => {
    onAnswerClarification = jest.fn().mockReturnValue(() => Promise.resolve({}));

    const store = createStore(combineReducers({ form: formReducer }));

    const props = {
      contest: { jid: 'contestJid' },
      clarification: { jid: 'clarificationJid123' },
      isBoxOpen: true,
      isBoxLoading: false,
      onToggleBox: () => {
        return;
      },
      onAnswerClarification,
    };
    wrapper = mount(
      <Provider store={store}>
        <ContestClarificationAnswerBox {...props} />
      </Provider>
    );
  });

  test('form', () => {
    const answer = wrapper.find('textarea[name="answer"]');
    answer.getDOMNode().value = 'Yes.';
    answer.simulate('input');

    const form = wrapper.find('form');
    form.simulate('submit');

    expect(onAnswerClarification).toHaveBeenCalledWith('contestJid', 'clarificationJid123', 'Yes.');
  });
});
