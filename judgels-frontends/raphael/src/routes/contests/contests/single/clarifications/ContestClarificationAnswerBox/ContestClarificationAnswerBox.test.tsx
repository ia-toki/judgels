import { mount, ReactWrapper } from 'enzyme';
import * as React from 'react';
import { Provider } from 'react-redux';
import { MemoryRouter } from 'react-router';
import { combineReducers, createStore } from 'redux';
import { reducer as formReducer } from 'redux-form';

import { contest, contestJid } from '../../../../../../fixtures/state';
import { ContestClarification } from '../../../../../../modules/api/uriel/contestClarification';

import { ContestClarificationAnswerBox, ContestClarificationAnswerBoxProps } from './ContestClarificationAnswerBox';

describe('ContestClarificationAnswerBox', () => {
  let onAnswerClarification: jest.Mock<any>;
  let wrapper: ReactWrapper<any, any>;

  const clarification = {
    jid: 'clarificationJid123',
  } as ContestClarification;

  beforeEach(() => {
    onAnswerClarification = jest.fn().mockReturnValue(() => Promise.resolve({}));

    const store: any = createStore(combineReducers({ form: formReducer }));

    const props: ContestClarificationAnswerBoxProps = {
      contest,
      clarification,
      isBoxOpen: true,
      isBoxLoading: false,
      onToggleBox: () => {
        return;
      },
      onAnswerClarification,
    };
    wrapper = mount(
      <Provider store={store}>
        <MemoryRouter>
          <ContestClarificationAnswerBox {...props} />
        </MemoryRouter>
      </Provider>
    );
  });

  test('answer clarification dialog form', () => {
    const answer = wrapper.find('textarea[name="answer"]');
    answer.simulate('change', { target: { value: 'Yes.' } });

    const form = wrapper.find('form');
    form.simulate('submit');

    expect(onAnswerClarification).toHaveBeenCalledWith(contestJid, 'clarificationJid123', 'Yes.', undefined);
  });
});
