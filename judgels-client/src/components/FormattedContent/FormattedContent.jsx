import { contestFileAPI } from '../../modules/api/uriel/contestFile';
import RichStatementText from '../RichStatementText/RichStatementText';

export function FormattedContent({ context, children }) {
  let res = children;
  res = formatDownloadUrls(res, context);

  return <RichStatementText>{res}</RichStatementText>;
}

function formatDownloadUrls(text, context) {
  const { contestJid } = context;
  if (!contestJid) {
    return text;
  }
  return text.replace(/(src|href)="download\//g, `$1="${contestFileAPI.renderDownloadFilesUrl(contestJid)}/`);
}
