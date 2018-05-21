import { mount, ReactWrapper } from 'enzyme';
import * as React from 'react';
import { Provider } from 'react-redux';
import { MemoryRouter } from 'react-router';
import { applyMiddleware, combineReducers, createStore } from 'redux';
import { reducer as formReducer } from 'redux-form';
import thunk from 'redux-thunk';

import { createContestProblemPage } from './ContestProblemPage';
import { urielReducer } from '../../../../../../../../../modules/urielReducer';
import { PutContest } from '../../../../../../../modules/contestReducer';
import { preferredGradingLanguage } from '../../../../../../../../../../../../modules/api/gabriel/language';
import { ContestProblemStatus } from '../../../../../../../../../../../../modules/api/uriel/contestProblem';
import { contest, contestJid, problemJid } from '../../../../../../../../../../../../fixtures/state';

describe('ContestProblemPage', () => {
  let contestProblemActions: jest.Mocked<any>;
  let contestSubmissionActions: jest.Mocked<any>;
  let wrapper: ReactWrapper<any, any>;

  beforeEach(() => {
    contestProblemActions = {
      fetchWorksheet: jest.fn().mockReturnValue(() =>
        Promise.resolve({
          contestantProblem: {
            problem: {
              problemJid,
              alias: 'C',
              status: ContestProblemStatus.Open,
              submissionsLimit: 0,
            },
            totalSubmissions: 2,
          },
          worksheet: {
            statement: {
              name: 'Problem',
              timeLimit: 2000,
              memoryLimit: 65536,
              text: 'Lorem ipsum',
            },
            submissionConfig: {
              sourceKeys: { encoder: 'Encoder', decoder: 'Decoder' },
              gradingEngine: 'Batch',
              gradingLanguageRestriction: { allowedLanguageNames: [] },
            },
          },
        })
      ),
    };
    contestSubmissionActions = {
      submit: jest.fn(),
    };

    const store = createStore(combineReducers({ form: formReducer, uriel: urielReducer }), applyMiddleware(thunk));
    store.dispatch(PutContest.create(contest));

    const ContestProblemPage = createContestProblemPage(contestProblemActions, contestSubmissionActions);

    wrapper = mount(
      <Provider store={store}>
        <MemoryRouter>
          <ContestProblemPage />
        </MemoryRouter>
      </Provider>
    );
  });

  test('submission form', async () => {
    await new Promise(resolve => setImmediate(resolve));
    wrapper.update();

    const encoder = wrapper.find('input[name="sourceFiles.encoder"]');
    encoder.simulate('change', { target: { files: [{ name: 'encoder.cpp', size: 1000 }] } });

    const decoder = wrapper.find('input[name="sourceFiles.decoder"]');
    decoder.simulate('change', { target: { files: [{ name: 'decoder.cpp', size: 2000 }] } });

    const form = wrapper.find('form');
    form.simulate('submit');

    expect(contestSubmissionActions.submit).toHaveBeenCalledWith(contestJid, problemJid, {
      gradingLanguage: preferredGradingLanguage,
      sourceFiles: {
        encoder: { name: 'encoder.cpp', size: 1000 } as File,
        decoder: { name: 'decoder.cpp', size: 2000 } as File,
      },
    });
  });
});
