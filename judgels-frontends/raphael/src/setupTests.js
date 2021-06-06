import { configure } from 'enzyme';
import Adapter from '@wojtekmaj/enzyme-adapter-react-17';
import nock from 'nock';

configure({ adapter: new Adapter() });

nock.disableNetConnect();

window.conf = {
  name: 'Judgels',
  slogan: 'Judgment Angels',
  apiUrls: {
    jophiel: 'http://jophiel',
    uriel: 'http://uriel',
    jerahmeel: 'http://jerahmeel',
  },
  welcomeBanner: {
    title: 'Welcome to Judgels',
    description:
      'Lorem ipsum dolor sit amet, consectetur adipisicing elit.Lorem ipsum dolor sit amet, consectetur adipisicing elit. Lorem ipsum dolor sit amet, consectetur adipisicing elit. Lorem ipsum dolor sit amet, consectetur adipisicing elit.',
  },
};

window.scrollTo = function() {
  return;
};
