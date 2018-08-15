import { mount, ReactWrapper } from 'enzyme';
import * as React from 'react';
import { IntlProvider } from 'react-intl';
import { combineReducers, createStore } from 'redux';
import { reducer as formReducer } from 'redux-form';
import { Provider } from 'react-redux';

import { contest } from 'fixtures/state';
import { ContestAnnouncement } from 'modules/api/uriel/contestAnnouncement';

import { ContestAnnouncementsPage, ContestAnnouncementsPageProps } from './ContestAnnouncementsPage';
import { ContestAnnouncementCard } from '../ContestAnnouncementCard/ContestAnnouncementCard';

describe('ContestAnnouncementsPage', () => {
  let wrapper: ReactWrapper<any, any>;
  let onGetPublishedAnnouncements: jest.Mock<any>;
  let onGetAnnouncementConfig: jest.Mock<any>;
  let onCreateAnnouncement: jest.Mock<any>;

  const render = () => {
    const props: ContestAnnouncementsPageProps = {
      contest,
      onGetPublishedAnnouncements,
      onGetAnnouncementConfig,
      onCreateAnnouncement,
    };

    const store = createStore(combineReducers({ form: formReducer }));

    wrapper = mount(
      <IntlProvider locale={navigator.language}>
        <Provider store={store}>
          <ContestAnnouncementsPage {...props} />
        </Provider>
      </IntlProvider>
    );
  };

  beforeEach(() => {
    onGetPublishedAnnouncements = jest.fn();
  });

  describe('when there are no announcements', () => {
    beforeEach(() => {
      onGetPublishedAnnouncements.mockReturnValue(Promise.resolve([]));
      render();
    });

    it('shows placeholder text and no announcements', async () => {
      await new Promise(resolve => setImmediate(resolve));
      wrapper.update();

      expect(wrapper.text()).toContain('No announcements.');
      expect(wrapper.find(ContestAnnouncementCard)).toHaveLength(0);
    });
  });

  describe('when there are announcements', () => {
    beforeEach(() => {
      const announcements: ContestAnnouncement[] = [
        {
          jid: 'jid1',
          userJid: 'userJid1',
          title: 'title1',
          content: 'content1',
          updatedTime: 0,
        } as ContestAnnouncement,
        {
          jid: 'jid2',
          userJid: 'userJid2',
          title: 'title2',
          content: 'content2',
          updatedTime: 0,
        } as ContestAnnouncement,
      ];
      onGetPublishedAnnouncements.mockReturnValue(Promise.resolve(announcements));

      render();
    });

    it('shows the announcements', async () => {
      await new Promise(resolve => setImmediate(resolve));
      wrapper.update();

      expect(wrapper.find(ContestAnnouncementCard)).toHaveLength(2);
    });
  });
});
