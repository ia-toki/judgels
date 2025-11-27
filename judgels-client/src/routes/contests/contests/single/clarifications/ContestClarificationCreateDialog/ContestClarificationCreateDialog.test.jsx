import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { Provider } from 'react-redux';
import createMockStore from 'redux-mock-store';

import { ContestClarificationCreateDialog } from './ContestClarificationCreateDialog';

describe('ContestClarificationCreateDialog', () => {
  let onCreateClarification;

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
    render(
      <Provider store={store}>
        <ContestClarificationCreateDialog {...props} />
      </Provider>
    );
  });

  test('form', async () => {
    const user = userEvent.setup();

    const button = screen.getByRole('button');
    await user.click(button);

    await screen.findByRole('dialog');

    // TODO(fushar): make this work
    // See https://github.com/FezVrasta/popper.js/issues/478

    // const topicJid = screen.getByRole('button', { name: /topic/i });
    // await user.click(topicJid);

    const title = screen.getByRole('textbox', { name: /title/i });
    await user.type(title, 'Snack');

    const question = screen.getByRole('textbox', { name: /question/i });
    await user.type(question, 'Is snack provided?');

    const submitButton = screen.getByRole('button', { name: /submit/i });
    await user.click(submitButton);

    expect(onCreateClarification).toHaveBeenCalledWith('contestJid', {
      topicJid: 'contestJid',
      title: 'Snack',
      question: 'Is snack provided?',
    });
  });
});
