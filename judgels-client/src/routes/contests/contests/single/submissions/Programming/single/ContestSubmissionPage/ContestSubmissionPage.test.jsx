import { mount } from 'enzyme';
import { Provider } from 'react-redux';
import { MemoryRouter, Route } from 'react-router';
import { applyMiddleware, combineReducers, createStore } from 'redux';
import thunk from 'redux-thunk';

import ContestSubmissionPage from './ContestSubmissionPage';
import { OutputOnlyOverrides } from '../../../../../../../../modules/api/gabriel/language';
import webPrefsReducer, { PutStatementLanguage } from '../../../../../../../../modules/webPrefs/webPrefsReducer';
import contestReducer, { PutContest } from '../../../../../modules/contestReducer';
import * as contestSubmissionActions from '../../modules/contestSubmissionActions';

jest.mock('../../modules/contestSubmissionActions');

describe('ContestSubmissionPage', () => {
  let wrapper;

  beforeEach(async () => {
    contestSubmissionActions.getSubmissionWithSource.mockReturnValue(() =>
      Promise.resolve({
        data: {
          submission: {
            id: 10,
            gradingEngine: OutputOnlyOverrides.KEY,
          },
          source: {},
        },
      })
    );

    const store = createStore(
      combineReducers({
        webPrefs: webPrefsReducer,
        uriel: combineReducers({ contest: contestReducer }),
      }),
      applyMiddleware(thunk)
    );
    store.dispatch(
      PutContest({
        jid: 'contestJid',
      })
    );
    store.dispatch(PutStatementLanguage('en'));

    wrapper = mount(
      <Provider store={store}>
        <MemoryRouter initialEntries={['/contest/contestSlug/submissions/submissions/10']}>
          <Route path="/contest/contestSlug/submissions/submissions/:submissionId" component={ContestSubmissionPage} />
        </MemoryRouter>
      </Provider>
    );

    await new Promise(resolve => setImmediate(resolve));
    wrapper.update();
  });

  test('page', () => {
    expect(wrapper.text()).toContain('Submission #10');
  });
});
