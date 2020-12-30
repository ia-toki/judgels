import { mount } from 'enzyme';
import { IntlProvider } from 'react-intl';
import { Provider } from 'react-redux';
import { applyMiddleware, combineReducers, createStore } from 'redux';
import { reducer as formReducer } from 'redux-form';
import thunk from 'redux-thunk';

import { parseDateTime } from '../../../../../../utils/datetime';
import { parseDuration } from '../../../../../../utils/duration';

import ContestEditGeneralTab from './ContestEditGeneralTab';
import { ContestStyle } from '../../../../../../modules/api/uriel/contest';
import contestReducer, { PutContest } from '../../../modules/contestReducer';
import * as contestActions from '../../../modules/contestActions';
import * as contestWebActions from '../../modules/contestWebActions';

jest.mock('../../../modules/contestActions');
jest.mock('../../modules/contestWebActions');

describe('ContestEditGeneralTab', () => {
  let wrapper;

  beforeEach(() => {
    contestWebActions.getContestByJidWithWebConfig.mockReturnValue(() => Promise.resolve({}));
    contestActions.updateContest.mockReturnValue(() => Promise.resolve({}));

    const store = createStore(
      combineReducers({ uriel: combineReducers({ contest: contestReducer }), form: formReducer }),
      applyMiddleware(thunk)
    );
    store.dispatch(PutContest({ jid: 'contestJid', slug: 'contest-a', style: ContestStyle.ICPC }));

    wrapper = mount(
      <IntlProvider locale={navigator.language}>
        <Provider store={store}>
          <ContestEditGeneralTab />
        </Provider>
      </IntlProvider>
    );
  });

  test('form', () => {
    const button = wrapper.find('button');
    button.simulate('click');

    wrapper.update();

    const slug = wrapper.find('input[name="slug"]');
    slug.simulate('change', { target: { value: 'contest-b' } });

    const name = wrapper.find('input[name="name"]');
    name.simulate('change', { target: { value: 'Contest B' } });

    const beginTime = wrapper.find('input[name="beginTime"]');
    beginTime.simulate('change', { target: { value: '2018-09-10 17:00' } });

    const duration = wrapper.find('input[name="duration"]');
    duration.simulate('change', { target: { value: '6h' } });

    const form = wrapper.find('form');
    form.simulate('submit');

    expect(contestActions.updateContest).toHaveBeenCalledWith('contestJid', 'contest-a', {
      slug: 'contest-b',
      name: 'Contest B',
      style: ContestStyle.ICPC,
      beginTime: parseDateTime('2018-09-10 17:00').getTime(),
      duration: parseDuration('6h'),
    });
  });
});
