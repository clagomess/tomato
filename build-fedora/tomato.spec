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

%install
rm -rf $RPM_BUILD_ROOT
mkdir -p $RPM_BUILD_ROOT/%{_bindir}
mkdir -p $RPM_BUILD_ROOT/%{_datadir}/tomato

# copy release
cp -R release/** $RPM_BUILD_ROOT/%{_datadir}/tomato

# copy executable
cp desktop/tomato.sh $RPM_BUILD_ROOT/%{_bindir}/tomato
echo "%{_datadir}/tomato/tomato-%{version}.jar" >> $RPM_BUILD_ROOT/%{_bindir}/tomato
chmod +x $RPM_BUILD_ROOT/%{_bindir}/tomato

# copy icons
mkdir -p $RPM_BUILD_ROOT/%{_datadir}/icons/hicolor/64x64/apps
mkdir -p $RPM_BUILD_ROOT/%{_datadir}/icons/hicolor/128x128/apps
cp desktop/icon-64x64.png $RPM_BUILD_ROOT/%{_datadir}/icons/hicolor/64x64/apps/tomato.png
cp desktop/icon-128x128.png $RPM_BUILD_ROOT/%{_datadir}/icons/hicolor/128x128/apps/tomato.png

# copy desktop entry
mkdir -p $RPM_BUILD_ROOT/%{_datadir}/applications
cp desktop/tomato.desktop $RPM_BUILD_ROOT/%{_datadir}/applications/tomato.desktop
echo "Version=%{version}" >> $RPM_BUILD_ROOT/%{_datadir}/applications/tomato.desktop

%files
%{_bindir}/tomato
%{_datadir}/tomato
%{_datadir}/icons/hicolor/64x64/apps/tomato.png
%{_datadir}/icons/hicolor/128x128/apps/tomato.png
%{_datadir}/applications/tomato.desktop

%changelog
