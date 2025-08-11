import Adapter from '@cfaester/enzyme-adapter-react-18';
import { configure } from 'enzyme';
import nock from 'nock';
import { TextDecoder, TextEncoder } from 'node:util';

global.TextEncoder = TextEncoder;
global.TextDecoder = TextDecoder;

configure({ adapter: new Adapter() });

nock.disableNetConnect();

window.setImmediate = function (fn) {
  setTimeout(fn, 0);
};

window.conf = {
  mode: 'TLX',
  name: 'Judgels',
  slogan: 'Judgment Angels',
  apiUrl: 'http://api',
  welcomeBanner: {
    title: 'Welcome to Judgels',
    description:
      'Lorem ipsum dolor sit amet, consectetur adipisicing elit.Lorem ipsum dolor sit amet, consectetur adipisicing elit. Lorem ipsum dolor sit amet, consectetur adipisicing elit. Lorem ipsum dolor sit amet, consectetur adipisicing elit.',
  },
};

window.scrollTo = function () {
  return;
};
