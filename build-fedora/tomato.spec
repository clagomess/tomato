Name:           tomato
Version:        0.0.0
Release:        1%{?dist}
Summary:        The open source and 100% offline REST Client tool.
BuildArch:      noarch

License:        MIT
URL:            https://github.com/clagomess/tomato
Source0:        %{name}-%{version}.tar.gz

Requires:       java-21-openjdk

%description
%{summary}

%prep
%setup -q

%build

%check

%global APP_ID io.github.clagomess.Tomato

%install
mkdir -p $RPM_BUILD_ROOT/%{_datadir}/tomato

# copy release
cp -R release/** $RPM_BUILD_ROOT/%{_datadir}/tomato

# config executable
install -v -Dm755 build-linux/tomato.sh $RPM_BUILD_ROOT/%{_bindir}/tomato
sed -i "s/\${TOMATO_HOME}/\/usr\/share\/tomato/" $RPM_BUILD_ROOT/%{_bindir}/tomato
sed -i "s/\${TOMATO_TAG}/${GIT_TAG}/" $RPM_BUILD_ROOT/%{_bindir}/tomato

# copy icons
install -v -Dm644 build-linux/icons/64x64.png $RPM_BUILD_ROOT/%{_datadir}/icons/hicolor/64x64/apps/%{APP_ID}.png
install -v -Dm644 build-linux/icons/128x128.png $RPM_BUILD_ROOT/%{_datadir}/icons/hicolor/128x128/apps/%{APP_ID}.png
install -v -Dm644 build-linux/icons/256x256.png $RPM_BUILD_ROOT/%{_datadir}/icons/hicolor/256x256/apps/%{APP_ID}.png
install -v -Dm644 build-linux/icons/scalable.svg $RPM_BUILD_ROOT/%{_datadir}/icons/hicolor/scalable/apps/%{APP_ID}.svg

# copy desktop entry
install -v -Dm644 build-linux/%{APP_ID}.desktop $RPM_BUILD_ROOT/%{_datadir}/applications/%{APP_ID}.desktop

%files
%{_bindir}/tomato
%{_datadir}/tomato
%{_datadir}/icons/hicolor/*/apps/%{APP_ID}.png
%{_datadir}/icons/hicolor/*/apps/%{APP_ID}.svg
%{_datadir}/applications/%{APP_ID}.desktop

%changelog
