import { mount, ReactWrapper } from 'enzyme';
import * as React from 'react';
import { Provider } from 'react-redux';
import { MemoryRouter } from 'react-router';
import { combineReducers, createStore } from 'redux';
import { reducer as formReducer } from 'redux-form';

import { contest, contestJid } from '../../../../../../fixtures/state';

import {
  ContestClarificationCreateDialog,
  ContestClarificationCreateDialogProps,
} from './ContestClarificationCreateDialog';

describe('ContestClarificationCreateDialog', () => {
  let onCreateClarification: jest.Mock<any>;
  let wrapper: ReactWrapper<any, any>;

  beforeEach(() => {
    onCreateClarification = jest.fn().mockReturnValue(() => Promise.resolve({}));

    const store: any = createStore(combineReducers({ form: formReducer }));

    const props: ContestClarificationCreateDialogProps = {
      contest,
      problemJids: ['problemJid1', 'problemJid2'],
      problemAliasesMap: { problemJid1: 'A', problemJid2: 'B' },
      problemNamesMap: { problemJid1: 'Problem 1', problemJid2: 'Problem 2' },
      statementLanguage: 'en',
      onCreateClarification,
    };
    wrapper = mount(
      <Provider store={store}>
        <MemoryRouter>
          <ContestClarificationCreateDialog {...props} />
        </MemoryRouter>
      </Provider>
    );
  });

  test('create clarification dialog form', () => {
    const button = wrapper.find('button');
    button.simulate('click');

    wrapper.update();

    // TODO(fushar): make this work
    // See https://github.com/FezVrasta/popper.js/issues/478

    // const topicJid = wrapper.find('button[data-key="topicJid"]');
    // topicJid.simulate('click');

    const title = wrapper.find('input[name="title"]');
    title.simulate('change', { target: { value: 'Snack' } });

    const question = wrapper.find('textarea[name="question"]');
    question.simulate('change', { target: { value: 'Is snack provided?' } });

    const form = wrapper.find('form');
    form.simulate('submit');

    expect(onCreateClarification).toHaveBeenCalledWith(contestJid, {
      topicJid: contestJid,
      title: 'Snack',
      question: 'Is snack provided?',
    });
  });
});
