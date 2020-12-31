import './Footer.css';

export function Footer() {
  return (
    <div className="footer">
      <hr />
      <div className="footer__text">
        <div className="footer__left">&copy; Ikatan Alumni TOKI</div>
        <div className="footer__right">
          Powered by <a href="https://github.com/ia-toki/judgels">Judgels</a>
        </div>
      </div>
    </div>
  );
}
