import { mount } from 'enzyme';
import { Provider } from 'react-redux';
import createMockStore from 'redux-mock-store';

import { ContestClarificationCreateDialog } from './ContestClarificationCreateDialog';

describe('ContestClarificationCreateDialog', () => {
  let onCreateClarification;
  let wrapper;

  beforeEach(() => {
    onCreateClarification = jest.fn().mockReturnValue(() => Promise.resolve({}));

    const store = createMockStore()({});

    const props = {
      contest: { jid: 'contestJid' },
      problemJids: ['problemJid1', 'problemJid2'],
      problemAliasesMap: { problemJid1: 'A', problemJid2: 'B' },
      problemNamesMap: { problemJid1: 'Problem 1', problemJid2: 'Problem 2' },
      statementLanguage: 'en',
      onCreateClarification,
    };
    wrapper = mount(
      <Provider store={store}>
        <ContestClarificationCreateDialog {...props} />
      </Provider>
    );
  });

  test('form', () => {
    const button = wrapper.find('button');
    button.simulate('click');

    // TODO(fushar): make this work
    // See https://github.com/FezVrasta/popper.js/issues/478

    // const topicJid = wrapper.find('button[data-key="topicJid"]');
    // topicJid.simulate('click');

    const title = wrapper.find('input[name="title"]');
    title.prop('onChange')({ target: { value: 'Snack' } });

    const question = wrapper.find('textarea[name="question"]');
    question.prop('onChange')({ target: { value: 'Is snack provided?' } });

    const form = wrapper.find('form');
    form.simulate('submit');

    expect(onCreateClarification).toHaveBeenCalledWith('contestJid', {
      topicJid: 'contestJid',
      title: 'Snack',
      question: 'Is snack provided?',
    });
  });
});
